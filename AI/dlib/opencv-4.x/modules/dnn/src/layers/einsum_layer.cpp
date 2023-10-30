// This file is part of OpenCV project.
// It is subject to the license terms in the LICENSE file found in the top-level directory
// of this distribution and at http://opencv.org/license.html.

#include <inttypes.h>
#include <opencv2/dnn/shape_utils.hpp>
#include "../precomp.hpp"
#include "layers_common.hpp"

namespace cv
{
namespace dnn
{

static bool IsTransposeReshapeForEinsum(const std::vector<size_t>& perm,
                                        std::vector<int> input_dims,
                                        MatShape& new_shape) {
    // As long as the dims with values > 1 stay in the same order, it's a reshape.
    // Example: Shape=(1,1,1024,4096) -> perm=(2,0,3,1).
    size_t last_permuted_axis = 0;
    for (size_t i = 0; i < perm.size(); ++i) {
        if (input_dims[perm[i]] == 1)
            continue;
        if (perm[i] < last_permuted_axis)
            return false;
        last_permuted_axis = perm[i];
    }
    new_shape.assign(input_dims.begin(), input_dims.end());
    for (size_t i = 0; i < perm.size(); ++i) {
        new_shape[i] = input_dims[perm[i]];
    }
    return true;
}

Mat batchwiseMatMul(
    const Mat& input1,
    const MatShape& input1ShapeOverride,
    const Mat& input2,
    const MatShape& input2ShapeOverride)
{
    // Sanity checks before the actual MatMul
    //input_1.DataType() == input_2.DataType(), "Data types of the inputs must match for MatMul");

    CV_CheckEQ(input1ShapeOverride.size(), (size_t) 3, "Only 1 batch dimension is allowed for MatMul");
    CV_CheckEQ(input2ShapeOverride.size(), (size_t) 3, "Only 1 batch dimension is allowed for MatMul");
    CV_CheckEQ((size_t) input1ShapeOverride[0], (size_t) input2ShapeOverride[0], "Batch dimension should match for MatMul;");
    CV_CheckEQ((size_t) input1ShapeOverride[2], (size_t) input2ShapeOverride[1], "Incompatible matrix dimensions for matMul");

    size_t batches = input1ShapeOverride[0];
    size_t M = input1ShapeOverride[1];
    size_t K = input1ShapeOverride[2];
    size_t N = input2ShapeOverride[2];

    //TODO: deal with dynamic shapes
    //TODO: deal with reshaping operation (it might not always be needed)
    std::vector<Mat> output;
    if (batches > 1)
    {
        Mat reshapedInput1 = input1;
        Mat reshapedInput2 = input2;

        // input1 should of size MxK
        // check if input1 needs reshape, if need reshape
        if (input1.size[0] != M || input1.size[1] != K)
        {
            int shape[] = {static_cast<int>(batches), static_cast<int>(M), static_cast<int>(K)};
            reshapedInput1 = input1.reshape(1, 3, shape);
        }

        // input2 should be of size KxN
        // check if input2 needs reshape, if needs reshape
        if (input2.size[0] != K || input2.size[1] != N)
        {
            int shape[] = {static_cast<int>(batches), static_cast<int>(K), static_cast<int>(N)};
            reshapedInput2 = input2.reshape(1, 3, shape);
        }

        for (size_t i=0; i < batches; i++)
        {
            std::vector<Range> ranges1 = {cv::Range(i, i+1)};
            for (int j = 1; j < reshapedInput1.dims; j++)
                ranges1.emplace_back(cv::Range::all());

            Mat part1 = reshapedInput1(ranges1);
            int shape[] = {static_cast<int>(M), static_cast<int>(K)};
            part1 = part1.reshape(1, sizeof(shape)/sizeof(shape[0]), shape);

            std::vector<Range> ranges2 = {cv::Range(i, i+1)};
            for (int j = 1; j < reshapedInput2.dims; j++)
                ranges2.emplace_back(cv::Range::all());

            Mat part2 = reshapedInput2(ranges2);
            int shape2[] = {static_cast<int>(K), static_cast<int>(N)};
            part2 = part2.reshape(1, sizeof(shape2)/sizeof(shape2[0]), shape2);

            Mat tmp_output;
            cv::gemm(part1, part2, 1.0, cv::Mat(), 1.0, tmp_output);
            int newShape[] = {1, static_cast<int>(M), static_cast<int>(N)};
            tmp_output = tmp_output.reshape(1, sizeof(newShape)/sizeof(newShape[0]), newShape);

            output.emplace_back(tmp_output);
        }

    } else {

        Mat reshapedInput1 = input1;
        Mat reshapedInput2 = input2;

        // input1 should of size MxK
        // check if input1 needs reshape, if need reshape
        if (input1.dims > 2 || input1.size[0] != M || input1.size[1] != K)
        {
            int shape[] = {static_cast<int>(M), static_cast<int>(K)};
            reshapedInput1 = input1.reshape(1, 2, shape);
        }

        // input2 should be of size KxN
        // check if input2 needs reshape, if needs reshape
        if (input2.dims > 2 || input2.size[0] != K || input2.size[1] != N)
        {
            int shape2[] = {static_cast<int>(K), static_cast<int>(N)};
            reshapedInput2 = input2.reshape(1, 2, shape2);
        }

        Mat tmp_output;
        cv::gemm(reshapedInput1, reshapedInput2, 1.0, cv::Mat(), 1.0, tmp_output);

        int newShape[] = {1, static_cast<int>(M), static_cast<int>(N)};
        tmp_output = tmp_output.reshape(1, sizeof(newShape)/sizeof(newShape[0]), newShape);
        output.emplace_back(tmp_output);

    }

    int outputDim[] = {static_cast<int>(output.size()), static_cast<int>(M), static_cast<int>(N)};
    Mat output_buffer = Mat::zeros(3, outputDim, CV_32F);

    for (size_t i = 0; i < output.size(); i++) {
        Mat output_slice = output_buffer.row(i);
        output[i].copyTo(output_slice);
    }
    return output_buffer;
};

Mat Transpose(
    const cv::Mat& input,
    const MatShape& input_shape_override,
    const std::vector<size_t> permutation)
{

    int input_rank = input_shape_override.size();

    CV_Assert(input_rank == permutation.size());

    // TODO: ouptimize
    bool reshape = false;
    if (input.dims != input_shape_override.size())
    {
        reshape = true;
    }

    Mat input_reshaped;
    if(reshape)
    {
        input_reshaped = input.reshape(1, input_shape_override.size(), input_shape_override.data());
    }

    MatShape outputDims;
    outputDims.reserve(input_rank);
    for (const auto& dim : permutation)
        outputDims.emplace_back(input_shape_override[dim]);

    Mat output;
    // TODO: ouptimize
    MatShape tmp_perm;
    tmp_perm.reserve(permutation.size());
    for (int i = 0; i < permutation.size(); i++)
        tmp_perm.emplace_back(static_cast<int>(permutation[i]));

    cv::transposeND((reshape ? input_reshaped : input), tmp_perm, output);
    return output;
}


bool IsTransposeRequired(size_t input_rank, const std::vector<size_t>& permutation) {
    CV_Assert(input_rank == permutation.size());

    // No transpose required for scalars
    if (input_rank == 0){
        return false;
    }

    // Weeds out cases where permutation is something like [0, 1, 2] for a 3D input and so on
    bool transpose_required = false;
    for (size_t i = 0; i < input_rank; ++i) {
        if (permutation[i] != i) {
            transpose_required = true;
            break;
        }
    }

  return transpose_required;
}

Mat Diagonal(
    const cv::Mat& input,
    int subscriptIndicesToInputIndex,
    int dimIndexInIreprocessedInput)
{
    CV_Error(Error::StsNotImplemented, "Diagonal Not Implemented Yet");
}

/**
 * Returns the index associated with the input character.
 * - Returns a value between 0 and 25 for inputs in the range 'a' to 'z'.
 * - Returns a value between 26 and 51 for inputs in the range 'A' to 'Z'.
 * - Returns -1 for invalid input that is not in the range 'a' to 'z' or 'A' to 'Z' (the caller should handle the returned result accordingly).
 */
int letterToIndex(const char ch) {
    if (ch >= 'a' && ch <= 'z') {
        return static_cast<int>(ch) - 'a';
    }

    if (ch >= 'A' && ch <= 'Z') {
        return static_cast<int>('z') + static_cast<int>(ch) - 'A';
    }
    // invalid character - return error value
    return -1;
}

// Implementation of the Einsum layer is heavily influenced by Onnxruntime at the time of writing.
// Main logic is borrowed from onnxrutime:
// https://github.com/microsoft/onnxruntime/blob/eaea34f8e29df9fb21fab675a3a895084407f306/onnxruntime/core/providers/cpu/math/einsum_utils/einsum_compute_preprocessor.cc#L8
class LayerEinsumImpl CV_FINAL : public EinsumLayer
{
private:
    Ptr<ReduceLayer> reduce;
public:
    // Number of inputs and outputs of the layer
    int numInputs;

    // inputShapes;
    std::vector<MatShape> einsumInpShapes;

    // Preprocessed inputs
    std::vector<Mat> preProcessedInputs;

    // This is container for preporcessed inputs
    std::vector<MatShape> homogenizedInputDims;

    // Collect outpus dimentions
    MatShape einsumOutDims; // vector to store output dimentions

    // These hold equation subring, left hand side and right it of
    String lhs_eq, rhs_eq;

    // Holds token from left hand side of the equation
    std::vector<String> lhs_eq_tokens;

    // Idicates if equation substring is defined in explit way such as "ij, jk->ik"
    // as opposed to "ij->"
    bool explicitEquation = false;

    // Stores the subscript indices for each input in the equation
    std::vector<std::vector<int>> inputSubscriptIndices;

    // Keeps track of the input index of the last input that had the subscript label
    // If the value is `-1`, it means the subscript label was never encountered or it appears in the output
    std::vector<int> subscriptIndicesToLastInput;

    // Holds the dimension value of the index corresponding to the subscript label
    // `-1` indicates that the corresponding label was not encountered at all
    std::vector<int> subscriptIndicesToDimValue;

    // Index corresponding to each output dim corresponding to each subscript index
    // A value of -1 means the corresponding subscript index is not found in the output
    std::vector<int> subscriptIndicesToOutputIndices;

    // Hold max number of alphabetic numbers
    static const size_t numOfLetters = 52;

    // Stores the count corresponding to each letter encountered
    // A value of `0` indicates that the corresponding letter hasn't been seen at all
    std::array<int, numOfLetters> letter2count;

    // Hold the assigned index corresponding to the letter seen
    // `-1` means the corresponding letter wasn't seen at all
    std::array<int, numOfLetters> letter2index;

    // Represents the count of unique subscript labels (subscript indices)
    // Example 1: For the equation 'ij, jk -> ik', num_subscript_indices_ = 3 (i, j, k)
    // Example 2: For the equation '...ij', 'jk' -> '...ik',
    // num_subscript_indices_ = 3 (i, j, k) + number of dimensions specified by an ellipsis (across all inputs)
    int numLetterIndices = 0;

    // The number of dimensions that are encompassed by an "ellipsis" - "...".
    size_t numOfEllipsisDims = 0;


    void parseEquation(String equation);
    void processEquation(const std::vector<MatShape>& inputs);
    void processBroadcastedDims();
    void createOutputSubsctipt();
    void calculateOutputShape();
    void preProcessInputs(InputArrayOfArrays& inputs);
    Mat reduceSum(Mat& src, MatShape& reduceAxis);
    Mat FinalizeOutput(const Mat& candidateOuput, const MatShape& ordered_subscript_indices_in_candidate);
    Mat pairwiseOperandProcess(
        const Mat& left,
        const MatShape& leftShapeOverride,
        const Mat& right,
        const MatShape& rightShapeOverride,
        const MatShape& reduceDims,
        bool isFinalPair
    );


    // constructor
    LayerEinsumImpl(const LayerParams& params)
    {
        setParamsFrom(params);
        String equation = params.get<String>("equation");
        int outputSize = params.get<int>("outputSize");
        numInputs  = params.get<int>("inputSize");

        CV_CheckEQ(outputSize, 1, "Einsum layer should only have one output");

        // get the input shapes from onnx importer
        for (int i=0; i < numInputs; i++){
            auto param = params.get("inputShapes" + cv::format("%d", i));
            int inputDims = param.size();
            std::vector<int> shape;
            for (int i = 0; i < inputDims; ++i)
                shape.emplace_back(param.get<int>(i));
            einsumInpShapes.emplace_back(shape);
        }


        // Maintains a mapping between input indices and their corresponding subscript labels for each input
        inputSubscriptIndices.reserve(numInputs);

        // We allocate space for 10 values as a precaution,
        // assuming that we won't encounter any input with a rank greater than 10.
        // In such cases, the value of num_subscript_indices_ would be greater than 10.
        subscriptIndicesToLastInput.reserve(10);
        subscriptIndicesToDimValue.reserve(10);

        // fill in vectors to avoid getting random numbers
        letter2count.fill(0);
        letter2index.fill(-1);

        // parser equation and extract tokens from the equation
        // save token to lhs_eq_tokens variable
        parseEquation(equation); // TODO: return lhs_eq_tokens

        // Start preprocessing related to equation parsing
        // and dimention broadcasting
        processEquation(einsumInpShapes);
        processBroadcastedDims();

        // calculate output shape
        createOutputSubsctipt();
        calculateOutputShape();
    }

    // getMeoryShapes
    bool getMemoryShapes(const std::vector<MatShape> &inputs,
                         const int requiredOutputs,
                         std::vector<MatShape> &outputs,
                         std::vector<MatShape> &internals) const CV_OVERRIDE
    {
        CV_UNUSED(internals);

        // check if passed and parsed inputs match up in number and dimensions
        CV_CheckEQ(static_cast<int>(inputs.size()), numInputs,
            "Number of inputs in forward and inputs during graph constructions do not match");
        for (int i = 0; i < numInputs; i++)
        {
            if (inputs[i] != einsumInpShapes[i])
                CV_Error(Error::StsAssert, "Passed input shapes do not match with parsed input shapes!");
        }

        outputs.clear();
        outputs.emplace_back(einsumOutDims);
        return true;

    } // getMemoryShape

    // forward
    void forward(InputArrayOfArrays inputs_arr,
                 OutputArrayOfArrays outputs_arr,
                 OutputArrayOfArrays internals_arr) CV_OVERRIDE
    {
        CV_TRACE_FUNCTION();
        CV_TRACE_ARG_VALUE(name, "name", name.c_str());

        if (inputs_arr.depth() == CV_16S)
        {
            forward_fallback(inputs_arr, outputs_arr, internals_arr);
            return;
        }

        // homogenize inputs
        preProcessInputs(inputs_arr);

        std::vector<cv::Mat> rawInputs, outputs;
        inputs_arr.getMatVector(rawInputs);
        outputs_arr.getMatVector(outputs);
        Mat result;

        // Pre-process the first input so as to reduce any dims that only it has
        {
            MatShape reducedDims;
            MatShape preservedDims;
            MatShape preservedShape;

            reducedDims.reserve(numLetterIndices);    // num_subscript_labels is the upper bound. No harm in over-reserving.
            preservedDims.reserve(numLetterIndices);  // num_subscript_labels is the upper bound. No harm in over-reserving.

            for (size_t i = 0; i < numLetterIndices; ++i) {
                if (subscriptIndicesToLastInput[i] == 0) {
                    reducedDims.push_back(i);
                } else {
                    preservedDims.push_back(i);
                }
            }

            // Reduce the dims that are last seen in the first input alone
            if (reducedDims.size() != 0)
            {
                result = reduceSum((!preProcessedInputs[0].empty() ? preProcessedInputs[0] : rawInputs[0]), reducedDims);
            } else {
                // Check if there is a pre-processed version of this input
                // If so assign it to result
                if (!preProcessedInputs[0].empty())
                {
                    result = preProcessedInputs[0];
                }
            }

            // Finalize the output at this stage if num_inputs == 1
            if (numInputs == 1) {
                // Finalize the output by applying any transpose required to get
                // it to the required output ordering and move it to the op's output
                result = FinalizeOutput(!result.empty() ? result : rawInputs[0], preservedDims);
            }
        }


        // Process the operands in a pair-wise fashion
        {
            bool isFinalPair = false;
            // Keep processing each input pair-wise
            for (int input = 1; input < numInputs; ++input) {
                MatShape reducedDims;
                reducedDims.reserve(numLetterIndices);  // num_subscript_labels is the upper bound. No harm in over-reserving by a small margin.
                for (int dim = 0; dim < numLetterIndices; ++dim)
                {
                    if (subscriptIndicesToLastInput[dim] == input)
                    {
                        // This is the last input we are seeing this dimension (and it doesn't occur in the output), so reduce along the dimension
                        reducedDims.push_back(dim);
                    }
                }

                if (input == numInputs - 1)
                    isFinalPair = true;

                // create temporary variable
                MatShape tmpResult;
                for (int i = 0; i < result.size.dims(); i++)
                    tmpResult.emplace_back(result.size[i]);


                // Use either the preprocessed inputs (if it is available) or the corresponding raw inputs
                result = pairwiseOperandProcess(!result.empty() ? result : rawInputs[0],
                                                !result.empty() ? tmpResult : homogenizedInputDims[0],
                                                !preProcessedInputs[input].empty() ? preProcessedInputs[input] : rawInputs[input],
                                                homogenizedInputDims[input],
                                                reducedDims,
                                                isFinalPair);
            }
        }

        // check of product of output dimentions and computed output dimentions match
        size_t reqProd = std::accumulate(einsumOutDims.begin(), einsumOutDims.end(), 1, std::multiplies<int>());
        MatShape realOutputDims = shape(result);
        size_t realProd = std::accumulate(realOutputDims.begin(), realOutputDims.end(), 1, std::multiplies<int>());

        CV_CheckEQ(reqProd, realProd, "Real output can not be shaped in to requred output");

        // reduce dimentions
        result = result.reshape(1, einsumOutDims.size(), einsumOutDims.data());
        result.copyTo(outputs[0]);
    } // forward
}; // EinsumClass

Mat LayerEinsumImpl::reduceSum(Mat& src, MatShape& reduceAxis)
{
    // initialize ReduceLayer
    LayerParams lp;
    lp.set("reduce", "SUM");
    int num_axes = reduceAxis.size();
    lp.set("axes", DictValue::arrayInt(&reduceAxis[0] , num_axes));
    reduce = ReduceLayer::create(lp);

    // Compute output shapes
    std::vector<MatShape> inputShapes{shape(src)};
    std::vector<MatShape> outputShapes, internalShapes;
    reduce->getMemoryShapes(inputShapes, 1, outputShapes, internalShapes);

    Mat output(outputShapes[0], CV_32F);

    std::vector<Mat> inputs;
    std::vector<Mat> outputs;
    std::vector<Mat> internals;
    inputs.emplace_back(src);
    outputs.emplace_back(output);

    reduce->forward(inputs, outputs, internals);
    return outputs[0];
}

void LayerEinsumImpl::preProcessInputs(InputArrayOfArrays& inputs_arr)
{
    std::vector<cv::Mat> inputs;
    inputs_arr.getMatVector(inputs);

    preProcessedInputs.reserve(inputs.size());
    homogenizedInputDims.reserve(inputs.size());

    int inputIter = 0;
    for(const Mat& input : inputs)
    {
        Mat preprocessed;

        // variable to hold processed version of the original input
        MatShape input_dims = shape(input);

        const auto& currSubscriptIndices = inputSubscriptIndices[inputIter];

        // There should be subscript index (subscript label) for each dim of the input
        CV_CheckEQ(input_dims.size(), currSubscriptIndices.size(),
            "Rank of the input must match number of subscript labels corresponding to the input");

        std::vector<int> subscriptIndicesToInputIndex(numLetterIndices, -1);
        // this will hold input dims after reordering so that all inputs have
        // same axes order
        MatShape homogenizedInputDims_(numLetterIndices, 1);

        int dimIndexInIreprocessedInput = 0;
        int dimIndexInOriginalInput = 0;

        for (const auto& subscriptIndex : currSubscriptIndices)
        {
            if(subscriptIndicesToInputIndex[subscriptIndex] == -1){
                subscriptIndicesToInputIndex[subscriptIndex] = dimIndexInIreprocessedInput++;
                homogenizedInputDims_[subscriptIndex] = input_dims[dimIndexInOriginalInput];
            } else {
                // Call diagonal
                preprocessed = Diagonal(
                    !preprocessed.empty() ? preprocessed : inputs[inputIter],
                    subscriptIndicesToInputIndex[subscriptIndex],
                    dimIndexInIreprocessedInput);
            }
            ++dimIndexInOriginalInput;
        }

        std::vector<size_t> permutation;
        for(auto& d : subscriptIndicesToInputIndex)
        {
            if (d != -1)
                permutation.emplace_back(d);
        }

        if (IsTransposeRequired(
            !preprocessed.empty() ? preprocessed.size.dims() : inputs[inputIter].size.dims(),
            permutation))
        {
            // call transpose
            preprocessed = Transpose(
                !preprocessed.empty() ? preprocessed : inputs[inputIter],
                !preprocessed.empty() ? shape(preprocessed) : shape(inputs[inputIter]),
                permutation);
        }

        if (!preprocessed.empty())
        {
            preprocessed = preprocessed.reshape(1, homogenizedInputDims_.size(), homogenizedInputDims_.data());
        }

        preProcessedInputs.emplace_back(preprocessed);
        homogenizedInputDims.emplace_back(homogenizedInputDims_);
        ++inputIter;
    }
}

void LayerEinsumImpl::parseEquation(String equation)
{
    // remove white spaces in the copy
    equation.erase(std::remove_if(equation.begin(), equation.end(), ::isspace), equation.end());

    // check if '->' - the output subscript label is present in the equation;
    std::size_t arrow_idx = equation.find("->");
    if (arrow_idx != std::string::npos)
    {
        // split left and righ hand sides of the equation
        lhs_eq = equation.substr(0, arrow_idx);
        rhs_eq = equation.substr(arrow_idx + 2);
        explicitEquation = true;
    } else {
        lhs_eq = equation;
    }

    // split lhs_eq by ',' - comma and put all created token - splits
    // into lhs_eq_tokens vector
    std::stringstream src(lhs_eq);
    for (std::string token; std::getline(src, token, ',');) {
        lhs_eq_tokens.emplace_back(token);
    }
}


void LayerEinsumImpl::calculateOutputShape()
{
    // Traverse through each of the subscript labels within the output subscript.
    bool middleOfEllipsis = false;
    // int64_t ellipsisCharCount = 0;

    subscriptIndicesToOutputIndices.resize(numLetterIndices, -1);

    std::array<int, numOfLetters> outputLetterToCount;
    outputLetterToCount.fill(0);

    int outputDimCounter = 0;
    for (auto letter : rhs_eq)
    {
        if(letter == '.')
        {
            CV_Error(Error::StsNotImplemented, "Ellipsis are not supported yet");
        } else {
            CV_CheckEQ(middleOfEllipsis, false,
                "Encountered '.' character that is not part of output subscript");

            auto letterIndex = letterToIndex(letter);

            CV_CheckNE(letterIndex, -1,
                "The only permissible subscript labels are lowercase letters (a-z) and uppercase letters (A-Z).");
            CV_CheckEQ(outputLetterToCount[letterIndex], 0,
                "Output subscript constains repeated letters");

            ++outputLetterToCount[letterIndex];
            auto mappedIndex = letter2index[letterIndex];

            CV_CheckNE(mappedIndex, -1,
                "Output subscript has letters that were not encountered in the inputs");

            // Push output dimention
            // Einsum layer only has one output vector
            einsumOutDims.emplace_back(subscriptIndicesToDimValue[mappedIndex]);

            // Reset the last input index for this subscript label
            // given that it is seen in the output and hence can't be reduced
            subscriptIndicesToLastInput[mappedIndex] = -1;
            subscriptIndicesToOutputIndices[mappedIndex] = outputDimCounter++;
        }
    }
}

void LayerEinsumImpl::createOutputSubsctipt()
{
    // The explicit form requires no operation, as the output
    // would have already been parsed during the input parsing process.
    if(explicitEquation)
    {
        // Ensure that the provided explicit equation includes an ellipsis if the input contains ellipses.
        if(numOfEllipsisDims > 0)
        {
            if(rhs_eq.find("...") == std::string::npos)
            {
                CV_Error(Error::StsError,
                "Provided output subscript does not include ellipsis while Inputs subscrits constain ellipsis");
            } else {
                CV_Error(Error::StsNotImplemented, "Ellipsis are not yet supported");
            }
        }
    }
}

void LayerEinsumImpl::processBroadcastedDims()
{
    // Only compute this function if ellipsis "..." was found in the equation
    if (numOfEllipsisDims > 0){
        // add assert inplace of return bool
        CV_Error(Error::StsError, "Ellipsis are not supperted currenly");
    }
}



void LayerEinsumImpl::processEquation(const std::vector<MatShape>& inputs)
{

    // Check if number of tokens in equal to number of inputs.
    // For install "ij, jk -> ik" needs to have 2 inputs tensors
    int num_input_tensors = inputs.size();
    CV_CheckEQ(static_cast<int>(lhs_eq_tokens.size()), num_input_tensors,
        "Number of input tensors does not match the number of subscripts in the input equation");

    int inputIdx = 0;
    for (const auto& token : lhs_eq_tokens)
    {
        const MatShape shape = inputs[inputIdx];
        size_t rank = shape.size();
        size_t dim_count = 0;

        std::vector<int> currTokenIndices;
        currTokenIndices.reserve(rank);

        // Variable to deal with "ellipsis" - '...' in the input
        bool middleOfellipsis = false;
        for (auto letter : token)
        {
            // Broadcasting based tokens are not implemented yet
            if (letter == '.')
            {
                CV_Error(Error::StsNotImplemented,
                 "Broad casting based indices are not supported currently");
            } else
            {

                if (middleOfellipsis)
                {
                    CV_Error(Error::StsAssert,
                    cv::format(
                        "Encountered '.' character that is not part of an ellipsis in the input: [%d]",
                        inputIdx));
                }

                int letterIdx = letterToIndex(letter);
                CV_CheckNE(letterIdx, -1,
                    "The only permissible subscript labels are lowercase letters (a-z) and uppercase letters (A-Z).");

                int dimValue = shape[dim_count];

                // The subscript label was not found in the global subscript label array
                // Therefore, it is added to both the local and global subscript arrays
                if(letter2count[letterIdx] == 0)
                {
                    letter2index[letterIdx] = numLetterIndices++;
                    subscriptIndicesToDimValue.push_back(dimValue);
                    subscriptIndicesToLastInput.push_back(inputIdx);

                } else {
                    // This letter has been seen in at least one other operand's subscript
                    // It must be equal unless one of them is a 1 (Numpy allows this)
                    auto mappedIndx = letter2index[letterIdx];
                    subscriptIndicesToLastInput[mappedIndx] = inputIdx;

                    if (subscriptIndicesToDimValue[mappedIndx] != dimValue)
                    {
                        if(subscriptIndicesToDimValue[mappedIndx] == 1){
                            //TODO: uncomment later on
                            // subscriptIndicesToDimValue[mappedIndx] == dimValue;
                        } else
                        {
                            if (dimValue != 1)
                            {
                                CV_Error(Error::StsError, cv::format("Einsum operands can not be broadcasted."
                                                                     "Check input shapes/equation passed."
                                                                     "Input shape of operand [%d]", inputIdx) +
                                                          cv::format(" is incompatible in the dimention [%zu].", static_cast<size_t>(dim_count)));
                            }
                        }
                    }
                }
                ++letter2count[letterIdx];
                currTokenIndices.push_back(letter2index[letterIdx]);

                CV_CheckLE(++dim_count, rank,
                    "The Einsum subscripts string has an excessive number of subscript labels compared to the rank of the input.");
            }
        }

        // When no broadcasting is requested, the number of subscript labels (dim_counter) should match the input's rank.
        CV_Assert(!(numOfEllipsisDims == 0 && dim_count != rank)
            && "The Einsum subscripts string does not contain required amount of subscript labels and no ellipsis is provided in the input.");

        inputSubscriptIndices.emplace_back(std::move(currTokenIndices));
        ++inputIdx;
    }
}

Mat LayerEinsumImpl::FinalizeOutput(
    const Mat& candidateOutput,
    const MatShape& ordered_subscript_indices_in_candidate)
{
    const std::vector<int>& subscript_indices_to_output_indices = subscriptIndicesToOutputIndices;
    const auto output_dims = einsumOutDims;

    MatShape output_shape = output_dims;
    const auto output_rank = output_dims.size();

    // CV_CheckEQ((int) candidateOutput.dims,  (int) output_shape.size(),
    //           "Einsum op: The candidate output cannot be reshaped into the op's output");

    const MatShape candidate_output_dims = MatShape(candidateOutput.size.p, candidateOutput.size.p + candidateOutput.dims);
    const int candidate_output_rank = candidate_output_dims.size();

    // This vector holds the shape of the candidate_output after removing the dims that have
    // been reduced in the final output
    MatShape candidate_output_shape_without_reduced_dims;
    candidate_output_shape_without_reduced_dims.reserve(candidate_output_rank);  // reserve upper bound

    // Identify the permutation required by the op's output
    std::vector<size_t> output_permutation;
    output_permutation.resize(output_rank, 0);
    size_t output_iter = 0;

    for (size_t iter = 0, end = ordered_subscript_indices_in_candidate.size(); iter < end; ++iter)
    {
        auto output_index = subscript_indices_to_output_indices[ordered_subscript_indices_in_candidate[iter]];

        // If output_index is -1, then this dimension does not show up in the op's output and has been reduced along the way
        if (output_index != -1)
        {
            output_permutation[output_index] = output_iter++;
            candidate_output_shape_without_reduced_dims.push_back(candidate_output_dims[iter]);
        } else {
            // This dim doesn't show up in the op's output and hence we check if the dim has been reduced in the candidate output
            CV_CheckEQ(candidate_output_dims[iter], 1,
            "Not all dimensions to be reduced have been reduced in the candidate output. Candidate output dims: "); //%d", candidateOutput.size));
        }
    }

    // Transpose to the required final output order
    // (Identify no-op transposes and prevent triggering the transpose)

    if (IsTransposeRequired(candidate_output_shape_without_reduced_dims.size(), output_permutation))
    {
        auto candidate_output_transposed = Transpose(
                                            candidateOutput,
                                            candidate_output_shape_without_reduced_dims,
                                            output_permutation);
        return candidate_output_transposed;
    }
    return candidateOutput;
}

Mat LayerEinsumImpl::pairwiseOperandProcess(
    const Mat& left,
    const MatShape& leftShapeOverride,
    const Mat& right,
    const MatShape& rightShapeOverride,
    const MatShape& reduceDims,
    bool isFinalPair
)
{
    size_t matDimSize = left.total();
    size_t overrideDimSize = total(leftShapeOverride);

    CV_CheckEQ(matDimSize, overrideDimSize, "Override dims are not compatible with left tensor shape");

    matDimSize = right.total();
    overrideDimSize = total(rightShapeOverride);

    CV_CheckEQ(matDimSize, overrideDimSize, "Override dims are not compatible with right tensor shape");

    // Make copy as this may be overridden downstream
    const auto& leftDims = leftShapeOverride;
    const auto& rightDims = rightShapeOverride;

    int leftRank = static_cast<int>(leftDims.size());
    int rightRank = static_cast<int>(rightDims.size());

    Mat currentLeft;
    Mat currentRight;

    CV_CheckEQ(leftRank, rightRank, "Raks of pair-wise operands must be equal");

    // Following vectors hold:
    // lro: dim indices that are present in left, right, and reduce_dims
    // lo: dim indices that are present in left and reduce_dims
    // ro: dim indices that are present in right and reduce_dims
    std::vector<size_t> lro;
    lro.reserve(5);  // Reserve an arbitrary amount of space for this vector (not bound to see a tensor of rank > kTensorShapeSmallBufferElementsSize)

    std::vector<size_t> lo;
    lo.reserve(5);  // Reserve an arbitrary amount of space for this vector (not bound to see a tensor of rank > kTensorShapeSmallBufferElementsSize)

    std::vector<size_t> ro;
    ro.reserve(5);  // Reserve an arbitrary amount of space for this vector (not bound to see a tensor of rank > kTensorShapeSmallBufferElementsSize)

    // Maintain sizes to create reshaped "views"
    int lro_size = 1;
    int lo_size = 1;
    int ro_size = 1;
    int reduced_size = 1;

    size_t reduceDimsIter = 0;
    size_t reduceDimsSize = reduceDims.size();

    for (int i = 0; i < leftRank; ++i)
    {
        int leftDim = leftDims[i];
        int rightDim = rightDims[i];

        bool hasLeftDim = leftDim > 1;    // non-trivial dimension (dim_value != 1)
        bool hasRightDim = rightDim > 1;  // non-trivial dimension (dim_value != 1)

        if (reduceDimsIter < reduceDimsSize && reduceDims[reduceDimsIter] == i)
        {
            // This dimension is to be reduced after this pair-wise operation
            ++reduceDimsIter;
            if (hasLeftDim && hasRightDim){
                // Both inputs have non-trivial dim values along this dimension
                // Both the left and right operands have non-trivial dimension value along this axis
                CV_CheckEQ(leftDim, rightDim, "Einsum op: Input dimensions must be equal along an axis to be reduced across all inputs");
                reduced_size *= leftDim;

            } else if (hasLeftDim){
                // if the dim to be reduced is only in one of left and right, we can reduce right away
                Mat tensorToReduce = !currentLeft.empty() ? currentLeft : left;
                MatShape shapeToReduce = !currentLeft.empty() ? shape(currentLeft) : leftDims;
                currentLeft = reduceSum(tensorToReduce, shapeToReduce);

            } else if (hasRightDim){
                Mat tensorToReduce = !currentRight.empty() ? currentRight : right;
                MatShape shapeToReduce = !currentRight.empty() ? shape(currentRight) : rightDims;
                currentLeft = reduceSum(tensorToReduce, shapeToReduce);
            }

        } else {
            // This dimension is not reduced (i.e.) it appears in the output after processing these 2 operands
            // Both the left and right operands have non-trivial dimension value along this axis
            // They must be equal
            if (hasLeftDim && hasRightDim){
                CV_CheckEQ(leftDim, rightDim, "Input shapes do not align");
                lro.push_back(i);
                lro_size *= leftDim;

            } else if (hasLeftDim) {
                // The left operand has non-trivial dimension value
                lo.push_back(i);
                lo_size *= leftDim;

            } else {
                // The right operand may or may not have non-trivial dim value
                // If it has trivial dim value (1),
                // it will just form a trailing dimension for the right operand
                ro.push_back(i);
                ro_size *= rightDim;
            }
        }
    }


    // Permutate the left operand so that the axes order go like this: [lro, lo, reduce_dims, ro]
    MatShape reshaped_dims;
    std::vector<size_t> left_permutation;
    left_permutation.reserve(lro.size() + lo.size() + reduceDims.size() + ro.size());
    left_permutation.insert(left_permutation.end(), lro.begin(), lro.end());
    left_permutation.insert(left_permutation.end(), lo.begin(), lo.end());
    //  left_permutation.insert(left_permutation.end(), reduce_dims.begin(), reduce_dims.end());

    for (auto& a : reduceDims)
    {
        left_permutation.push_back(a);
    }
    left_permutation.insert(left_permutation.end(), ro.begin(), ro.end());

    if (IsTransposeRequired(!currentLeft.empty() ? currentLeft.dims : leftDims.size(),
                                        left_permutation))
    {
        if (!currentLeft.empty() && IsTransposeReshapeForEinsum(left_permutation,
                                                                shape(currentLeft),
                                                                reshaped_dims))
        {
            // This can be done because curent_* tensors (if they exist) and output tensors are
            // intermediate tensors and cannot be input tensors to the Einsum node itself
            // (which are immutable).
            currentLeft = currentLeft.reshape(1, reshaped_dims.size(), reshaped_dims.data());
        } else {
            // Covered by ExplicitEinsumAsTensorContraction, DiagonalWithMatmul, ...
            currentLeft = Transpose(!currentLeft.empty() ? currentLeft: left,
                                    !currentLeft.empty() ? shape(currentLeft) : leftDims,
                                    left_permutation);
        }
    }

    // Permutate the right operand so that the axes order go like this: [lro, reduce_dims, ro, lo]
    std::vector<size_t> right_permutation;
    right_permutation.reserve(lro.size() + lo.size() + reduceDims.size() + ro.size());
    right_permutation.insert(right_permutation.end(), lro.begin(), lro.end());
    //  right_permutation.insert(right_permutation.end(), reduce_dims.begin(), reduce_dims.end());
    for (auto& a : reduceDims) {
        right_permutation.push_back(a);
    }
    right_permutation.insert(right_permutation.end(), ro.begin(), ro.end());
    right_permutation.insert(right_permutation.end(), lo.begin(), lo.end());

    if (IsTransposeRequired(!currentRight.empty() ? currentRight.dims: rightDims.size(),
                                        right_permutation))
    {
        if (!currentRight.empty() && IsTransposeReshapeForEinsum(right_permutation,
                                                                shape(currentRight),
                                                                reshaped_dims))
        {
            currentRight = currentRight.reshape(1, reshaped_dims.size(), reshaped_dims.data());
        } else {
            currentRight = Transpose(!currentRight.empty() ? currentRight : right,
                                    !currentRight.empty() ? shape(currentRight) : rightDims,
                                    right_permutation);
        }
    }

    // Calculate output size
    // Output shape will be determined by rules of MatMul:
    // because we are multiplying two tensors of shapes [lro, lo, reduce_dims] , [lro, reduce_dims, ro]
    // [dim_value of `lro` dims,
    //  dim_value of `lo` dims,
    // `1` for each of the `reduce_dims`,
    // dim_value of `ro` dims]
    MatShape outputDims;
    outputDims.reserve(lro.size() + lo.size() + reduceDims.size() + ro.size());
    for (size_t i = 0; i < lro.size(); ++i)
    {
        outputDims.emplace_back(leftDims[lro[i]]);
    }

    for (size_t i = 0; i < lo.size(); ++i)
    {
        outputDims.emplace_back(leftDims[lo[i]]);
    }

    for (size_t i = 0; i < reduceDims.size(); ++i)
    {
        outputDims.emplace_back(1);  // reduced dimensions will have a value 1 in it
    }

    for (size_t i = 0; i < ro.size(); ++i) {
        outputDims.emplace_back(rightDims[ro[i]]);
    }

    MatShape currentSubscriptOrder;
    // Calculate output permutation
    // After the MatMul op, the because the two operands have been permutated,
    // the output is permutated as well with respect to the original ordering of the axes.
    // The permutated order will be the dims in: [lro, lo, reduced_dims, ro]
    // Hence invert the permutation by a permutation that puts the axes in the same ordering
    std::vector<size_t> outputPermutation;
    if (!isFinalPair) {  // If this is not the final pair, we need to permutate the result to match the pre-fixed order for the next iteration
        outputPermutation.resize(lro.size() + lo.size() + reduceDims.size() + ro.size(), 0);
        size_t iter = 0;
        for (size_t i = 0; i < lro.size(); ++i)
        {
            outputPermutation[lro[i]] = iter++;
        }

        for (size_t i = 0; i < lo.size(); ++i)
        {
            outputPermutation[lo[i]] = iter++;
        }

        for (size_t i = 0; i < reduceDims.size(); ++i)
        {
            outputPermutation[reduceDims[i]] = iter++;
        }

        for (size_t i = 0; i < ro.size(); ++i)
        {
            outputPermutation[ro[i]] = iter++;
        }

    } else {
        currentSubscriptOrder.reserve(lro.size() + lo.size() + reduceDims.size() + ro.size());
        currentSubscriptOrder.insert(currentSubscriptOrder.end(), lro.begin(), lro.end());
        currentSubscriptOrder.insert(currentSubscriptOrder.end(), lo.begin(), lo.end());
        currentSubscriptOrder.insert(currentSubscriptOrder.end(), reduceDims.begin(), reduceDims.end());
        currentSubscriptOrder.insert(currentSubscriptOrder.end(), ro.begin(), ro.end());
    }

    Mat output = batchwiseMatMul(
        !currentLeft.empty() ? currentLeft : left,
        MatShape({static_cast<int>(lro_size), static_cast<int>(lo_size), static_cast<int>(reduced_size)}),
        !currentRight.empty() ? currentRight : right,
        MatShape({static_cast<int>(lro_size), static_cast<int>(reduced_size), static_cast<int>(ro_size)})
        );

    //reshape
    output = output.reshape(1, outputDims.size(), outputDims.data());

    if (!isFinalPair)
    {  // This is not the final pair - so bring the axes order to what the inputs conformed to
        if (IsTransposeRequired(outputDims.size(), outputPermutation))
        {
            if (IsTransposeReshapeForEinsum(outputPermutation,
                                            outputDims,
                                            reshaped_dims))
            {
                // See note following the previous call of function IsTransposeReshapeForEinsum.
                // Covered by ExplicitEinsumAsTensorContractionReshapeFinal.
                output = output.reshape(1, reshaped_dims.size(), reshaped_dims.data());
            }
        } else {
            output = Transpose(
                output,
                outputDims,
                outputPermutation);
        }
    } else {  // This is the final pair - Transpose directly to the output ordering required and copy the contents to the op's output
        // not sure if this finalize shape is needed at all
        output = FinalizeOutput(output, currentSubscriptOrder);
    }
    return output;
};

Ptr<EinsumLayer> EinsumLayer::create(const LayerParams& params)
{
    return makePtr<LayerEinsumImpl>(params);
}

}} // namespace cv::dnn
