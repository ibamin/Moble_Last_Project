// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: tensor_shape.proto

#ifndef GOOGLE_PROTOBUF_INCLUDED_tensor_5fshape_2eproto
#define GOOGLE_PROTOBUF_INCLUDED_tensor_5fshape_2eproto

#include <limits>
#include <string>

#include <google/protobuf/port_def.inc>
#if PROTOBUF_VERSION < 3019000
#error This file was generated by a newer version of protoc which is
#error incompatible with your Protocol Buffer headers. Please update
#error your headers.
#endif
#if 3019001 < PROTOBUF_MIN_PROTOC_VERSION
#error This file was generated by an older version of protoc which is
#error incompatible with your Protocol Buffer headers. Please
#error regenerate this file with a newer version of protoc.
#endif

#include <google/protobuf/port_undef.inc>
#include <google/protobuf/io/coded_stream.h>
#include <google/protobuf/arena.h>
#include <google/protobuf/arenastring.h>
#include <google/protobuf/generated_message_table_driven.h>
#include <google/protobuf/generated_message_util.h>
#include <google/protobuf/metadata_lite.h>
#include <google/protobuf/generated_message_reflection.h>
#include <google/protobuf/message.h>
#include <google/protobuf/repeated_field.h>  // IWYU pragma: export
#include <google/protobuf/extension_set.h>  // IWYU pragma: export
#include <google/protobuf/unknown_field_set.h>
// @@protoc_insertion_point(includes)
#include <google/protobuf/port_def.inc>
#define PROTOBUF_INTERNAL_EXPORT_tensor_5fshape_2eproto
PROTOBUF_NAMESPACE_OPEN
namespace internal {
class AnyMetadata;
}  // namespace internal
PROTOBUF_NAMESPACE_CLOSE

// Internal implementation detail -- do not use these members.
struct TableStruct_tensor_5fshape_2eproto {
  static const ::PROTOBUF_NAMESPACE_ID::internal::ParseTableField entries[]
    PROTOBUF_SECTION_VARIABLE(protodesc_cold);
  static const ::PROTOBUF_NAMESPACE_ID::internal::AuxiliaryParseTableField aux[]
    PROTOBUF_SECTION_VARIABLE(protodesc_cold);
  static const ::PROTOBUF_NAMESPACE_ID::internal::ParseTable schema[2]
    PROTOBUF_SECTION_VARIABLE(protodesc_cold);
  static const ::PROTOBUF_NAMESPACE_ID::internal::FieldMetadata field_metadata[];
  static const ::PROTOBUF_NAMESPACE_ID::internal::SerializationTable serialization_table[];
  static const uint32_t offsets[];
};
extern const ::PROTOBUF_NAMESPACE_ID::internal::DescriptorTable descriptor_table_tensor_5fshape_2eproto;
namespace opencv_tensorflow {
class TensorShapeProto;
struct TensorShapeProtoDefaultTypeInternal;
extern TensorShapeProtoDefaultTypeInternal _TensorShapeProto_default_instance_;
class TensorShapeProto_Dim;
struct TensorShapeProto_DimDefaultTypeInternal;
extern TensorShapeProto_DimDefaultTypeInternal _TensorShapeProto_Dim_default_instance_;
}  // namespace opencv_tensorflow
PROTOBUF_NAMESPACE_OPEN
template<> ::opencv_tensorflow::TensorShapeProto* Arena::CreateMaybeMessage<::opencv_tensorflow::TensorShapeProto>(Arena*);
template<> ::opencv_tensorflow::TensorShapeProto_Dim* Arena::CreateMaybeMessage<::opencv_tensorflow::TensorShapeProto_Dim>(Arena*);
PROTOBUF_NAMESPACE_CLOSE
namespace opencv_tensorflow {

// ===================================================================

class TensorShapeProto_Dim final :
    public ::PROTOBUF_NAMESPACE_ID::Message /* @@protoc_insertion_point(class_definition:opencv_tensorflow.TensorShapeProto.Dim) */ {
 public:
  inline TensorShapeProto_Dim() : TensorShapeProto_Dim(nullptr) {}
  ~TensorShapeProto_Dim() override;
  explicit constexpr TensorShapeProto_Dim(::PROTOBUF_NAMESPACE_ID::internal::ConstantInitialized);

  TensorShapeProto_Dim(const TensorShapeProto_Dim& from);
  TensorShapeProto_Dim(TensorShapeProto_Dim&& from) noexcept
    : TensorShapeProto_Dim() {
    *this = ::std::move(from);
  }

  inline TensorShapeProto_Dim& operator=(const TensorShapeProto_Dim& from) {
    CopyFrom(from);
    return *this;
  }
  inline TensorShapeProto_Dim& operator=(TensorShapeProto_Dim&& from) noexcept {
    if (this == &from) return *this;
    if (GetOwningArena() == from.GetOwningArena()
  #ifdef PROTOBUF_FORCE_COPY_IN_MOVE
        && GetOwningArena() != nullptr
  #endif  // !PROTOBUF_FORCE_COPY_IN_MOVE
    ) {
      InternalSwap(&from);
    } else {
      CopyFrom(from);
    }
    return *this;
  }

  static const ::PROTOBUF_NAMESPACE_ID::Descriptor* descriptor() {
    return GetDescriptor();
  }
  static const ::PROTOBUF_NAMESPACE_ID::Descriptor* GetDescriptor() {
    return default_instance().GetMetadata().descriptor;
  }
  static const ::PROTOBUF_NAMESPACE_ID::Reflection* GetReflection() {
    return default_instance().GetMetadata().reflection;
  }
  static const TensorShapeProto_Dim& default_instance() {
    return *internal_default_instance();
  }
  static inline const TensorShapeProto_Dim* internal_default_instance() {
    return reinterpret_cast<const TensorShapeProto_Dim*>(
               &_TensorShapeProto_Dim_default_instance_);
  }
  static constexpr int kIndexInFileMessages =
    0;

  friend void swap(TensorShapeProto_Dim& a, TensorShapeProto_Dim& b) {
    a.Swap(&b);
  }
  inline void Swap(TensorShapeProto_Dim* other) {
    if (other == this) return;
  #ifdef PROTOBUF_FORCE_COPY_IN_SWAP
    if (GetOwningArena() != nullptr &&
        GetOwningArena() == other->GetOwningArena()) {
   #else  // PROTOBUF_FORCE_COPY_IN_SWAP
    if (GetOwningArena() == other->GetOwningArena()) {
  #endif  // !PROTOBUF_FORCE_COPY_IN_SWAP
      InternalSwap(other);
    } else {
      ::PROTOBUF_NAMESPACE_ID::internal::GenericSwap(this, other);
    }
  }
  void UnsafeArenaSwap(TensorShapeProto_Dim* other) {
    if (other == this) return;
    GOOGLE_DCHECK(GetOwningArena() == other->GetOwningArena());
    InternalSwap(other);
  }

  // implements Message ----------------------------------------------

  TensorShapeProto_Dim* New(::PROTOBUF_NAMESPACE_ID::Arena* arena = nullptr) const final {
    return CreateMaybeMessage<TensorShapeProto_Dim>(arena);
  }
  using ::PROTOBUF_NAMESPACE_ID::Message::CopyFrom;
  void CopyFrom(const TensorShapeProto_Dim& from);
  using ::PROTOBUF_NAMESPACE_ID::Message::MergeFrom;
  void MergeFrom(const TensorShapeProto_Dim& from);
  private:
  static void MergeImpl(::PROTOBUF_NAMESPACE_ID::Message* to, const ::PROTOBUF_NAMESPACE_ID::Message& from);
  public:
  PROTOBUF_ATTRIBUTE_REINITIALIZES void Clear() final;
  bool IsInitialized() const final;

  size_t ByteSizeLong() const final;
  const char* _InternalParse(const char* ptr, ::PROTOBUF_NAMESPACE_ID::internal::ParseContext* ctx) final;
  uint8_t* _InternalSerialize(
      uint8_t* target, ::PROTOBUF_NAMESPACE_ID::io::EpsCopyOutputStream* stream) const final;
  int GetCachedSize() const final { return _cached_size_.Get(); }

  private:
  void SharedCtor();
  void SharedDtor();
  void SetCachedSize(int size) const final;
  void InternalSwap(TensorShapeProto_Dim* other);

  private:
  friend class ::PROTOBUF_NAMESPACE_ID::internal::AnyMetadata;
  static ::PROTOBUF_NAMESPACE_ID::StringPiece FullMessageName() {
    return "opencv_tensorflow.TensorShapeProto.Dim";
  }
  protected:
  explicit TensorShapeProto_Dim(::PROTOBUF_NAMESPACE_ID::Arena* arena,
                       bool is_message_owned = false);
  private:
  static void ArenaDtor(void* object);
  inline void RegisterArenaDtor(::PROTOBUF_NAMESPACE_ID::Arena* arena);
  public:

  static const ClassData _class_data_;
  const ::PROTOBUF_NAMESPACE_ID::Message::ClassData*GetClassData() const final;

  ::PROTOBUF_NAMESPACE_ID::Metadata GetMetadata() const final;

  // nested types ----------------------------------------------------

  // accessors -------------------------------------------------------

  enum : int {
    kNameFieldNumber = 2,
    kSizeFieldNumber = 1,
  };
  // string name = 2;
  void clear_name();
  const std::string& name() const;
  template <typename ArgT0 = const std::string&, typename... ArgT>
  void set_name(ArgT0&& arg0, ArgT... args);
  std::string* mutable_name();
  PROTOBUF_NODISCARD std::string* release_name();
  void set_allocated_name(std::string* name);
  private:
  const std::string& _internal_name() const;
  inline PROTOBUF_ALWAYS_INLINE void _internal_set_name(const std::string& value);
  std::string* _internal_mutable_name();
  public:

  // int64 size = 1;
  void clear_size();
  int64_t size() const;
  void set_size(int64_t value);
  private:
  int64_t _internal_size() const;
  void _internal_set_size(int64_t value);
  public:

  // @@protoc_insertion_point(class_scope:opencv_tensorflow.TensorShapeProto.Dim)
 private:
  class _Internal;

  template <typename T> friend class ::PROTOBUF_NAMESPACE_ID::Arena::InternalHelper;
  typedef void InternalArenaConstructable_;
  typedef void DestructorSkippable_;
  ::PROTOBUF_NAMESPACE_ID::internal::ArenaStringPtr name_;
  int64_t size_;
  mutable ::PROTOBUF_NAMESPACE_ID::internal::CachedSize _cached_size_;
  friend struct ::TableStruct_tensor_5fshape_2eproto;
};
// -------------------------------------------------------------------

class TensorShapeProto final :
    public ::PROTOBUF_NAMESPACE_ID::Message /* @@protoc_insertion_point(class_definition:opencv_tensorflow.TensorShapeProto) */ {
 public:
  inline TensorShapeProto() : TensorShapeProto(nullptr) {}
  ~TensorShapeProto() override;
  explicit constexpr TensorShapeProto(::PROTOBUF_NAMESPACE_ID::internal::ConstantInitialized);

  TensorShapeProto(const TensorShapeProto& from);
  TensorShapeProto(TensorShapeProto&& from) noexcept
    : TensorShapeProto() {
    *this = ::std::move(from);
  }

  inline TensorShapeProto& operator=(const TensorShapeProto& from) {
    CopyFrom(from);
    return *this;
  }
  inline TensorShapeProto& operator=(TensorShapeProto&& from) noexcept {
    if (this == &from) return *this;
    if (GetOwningArena() == from.GetOwningArena()
  #ifdef PROTOBUF_FORCE_COPY_IN_MOVE
        && GetOwningArena() != nullptr
  #endif  // !PROTOBUF_FORCE_COPY_IN_MOVE
    ) {
      InternalSwap(&from);
    } else {
      CopyFrom(from);
    }
    return *this;
  }

  static const ::PROTOBUF_NAMESPACE_ID::Descriptor* descriptor() {
    return GetDescriptor();
  }
  static const ::PROTOBUF_NAMESPACE_ID::Descriptor* GetDescriptor() {
    return default_instance().GetMetadata().descriptor;
  }
  static const ::PROTOBUF_NAMESPACE_ID::Reflection* GetReflection() {
    return default_instance().GetMetadata().reflection;
  }
  static const TensorShapeProto& default_instance() {
    return *internal_default_instance();
  }
  static inline const TensorShapeProto* internal_default_instance() {
    return reinterpret_cast<const TensorShapeProto*>(
               &_TensorShapeProto_default_instance_);
  }
  static constexpr int kIndexInFileMessages =
    1;

  friend void swap(TensorShapeProto& a, TensorShapeProto& b) {
    a.Swap(&b);
  }
  inline void Swap(TensorShapeProto* other) {
    if (other == this) return;
  #ifdef PROTOBUF_FORCE_COPY_IN_SWAP
    if (GetOwningArena() != nullptr &&
        GetOwningArena() == other->GetOwningArena()) {
   #else  // PROTOBUF_FORCE_COPY_IN_SWAP
    if (GetOwningArena() == other->GetOwningArena()) {
  #endif  // !PROTOBUF_FORCE_COPY_IN_SWAP
      InternalSwap(other);
    } else {
      ::PROTOBUF_NAMESPACE_ID::internal::GenericSwap(this, other);
    }
  }
  void UnsafeArenaSwap(TensorShapeProto* other) {
    if (other == this) return;
    GOOGLE_DCHECK(GetOwningArena() == other->GetOwningArena());
    InternalSwap(other);
  }

  // implements Message ----------------------------------------------

  TensorShapeProto* New(::PROTOBUF_NAMESPACE_ID::Arena* arena = nullptr) const final {
    return CreateMaybeMessage<TensorShapeProto>(arena);
  }
  using ::PROTOBUF_NAMESPACE_ID::Message::CopyFrom;
  void CopyFrom(const TensorShapeProto& from);
  using ::PROTOBUF_NAMESPACE_ID::Message::MergeFrom;
  void MergeFrom(const TensorShapeProto& from);
  private:
  static void MergeImpl(::PROTOBUF_NAMESPACE_ID::Message* to, const ::PROTOBUF_NAMESPACE_ID::Message& from);
  public:
  PROTOBUF_ATTRIBUTE_REINITIALIZES void Clear() final;
  bool IsInitialized() const final;

  size_t ByteSizeLong() const final;
  const char* _InternalParse(const char* ptr, ::PROTOBUF_NAMESPACE_ID::internal::ParseContext* ctx) final;
  uint8_t* _InternalSerialize(
      uint8_t* target, ::PROTOBUF_NAMESPACE_ID::io::EpsCopyOutputStream* stream) const final;
  int GetCachedSize() const final { return _cached_size_.Get(); }

  private:
  void SharedCtor();
  void SharedDtor();
  void SetCachedSize(int size) const final;
  void InternalSwap(TensorShapeProto* other);

  private:
  friend class ::PROTOBUF_NAMESPACE_ID::internal::AnyMetadata;
  static ::PROTOBUF_NAMESPACE_ID::StringPiece FullMessageName() {
    return "opencv_tensorflow.TensorShapeProto";
  }
  protected:
  explicit TensorShapeProto(::PROTOBUF_NAMESPACE_ID::Arena* arena,
                       bool is_message_owned = false);
  private:
  static void ArenaDtor(void* object);
  inline void RegisterArenaDtor(::PROTOBUF_NAMESPACE_ID::Arena* arena);
  public:

  static const ClassData _class_data_;
  const ::PROTOBUF_NAMESPACE_ID::Message::ClassData*GetClassData() const final;

  ::PROTOBUF_NAMESPACE_ID::Metadata GetMetadata() const final;

  // nested types ----------------------------------------------------

  typedef TensorShapeProto_Dim Dim;

  // accessors -------------------------------------------------------

  enum : int {
    kDimFieldNumber = 2,
    kUnknownRankFieldNumber = 3,
  };
  // repeated .opencv_tensorflow.TensorShapeProto.Dim dim = 2;
  int dim_size() const;
  private:
  int _internal_dim_size() const;
  public:
  void clear_dim();
  ::opencv_tensorflow::TensorShapeProto_Dim* mutable_dim(int index);
  ::PROTOBUF_NAMESPACE_ID::RepeatedPtrField< ::opencv_tensorflow::TensorShapeProto_Dim >*
      mutable_dim();
  private:
  const ::opencv_tensorflow::TensorShapeProto_Dim& _internal_dim(int index) const;
  ::opencv_tensorflow::TensorShapeProto_Dim* _internal_add_dim();
  public:
  const ::opencv_tensorflow::TensorShapeProto_Dim& dim(int index) const;
  ::opencv_tensorflow::TensorShapeProto_Dim* add_dim();
  const ::PROTOBUF_NAMESPACE_ID::RepeatedPtrField< ::opencv_tensorflow::TensorShapeProto_Dim >&
      dim() const;

  // bool unknown_rank = 3;
  void clear_unknown_rank();
  bool unknown_rank() const;
  void set_unknown_rank(bool value);
  private:
  bool _internal_unknown_rank() const;
  void _internal_set_unknown_rank(bool value);
  public:

  // @@protoc_insertion_point(class_scope:opencv_tensorflow.TensorShapeProto)
 private:
  class _Internal;

  template <typename T> friend class ::PROTOBUF_NAMESPACE_ID::Arena::InternalHelper;
  typedef void InternalArenaConstructable_;
  typedef void DestructorSkippable_;
  ::PROTOBUF_NAMESPACE_ID::RepeatedPtrField< ::opencv_tensorflow::TensorShapeProto_Dim > dim_;
  bool unknown_rank_;
  mutable ::PROTOBUF_NAMESPACE_ID::internal::CachedSize _cached_size_;
  friend struct ::TableStruct_tensor_5fshape_2eproto;
};
// ===================================================================


// ===================================================================

#ifdef __GNUC__
  #pragma GCC diagnostic push
  #pragma GCC diagnostic ignored "-Wstrict-aliasing"
#endif  // __GNUC__
// TensorShapeProto_Dim

// int64 size = 1;
inline void TensorShapeProto_Dim::clear_size() {
  size_ = int64_t{0};
}
inline int64_t TensorShapeProto_Dim::_internal_size() const {
  return size_;
}
inline int64_t TensorShapeProto_Dim::size() const {
  // @@protoc_insertion_point(field_get:opencv_tensorflow.TensorShapeProto.Dim.size)
  return _internal_size();
}
inline void TensorShapeProto_Dim::_internal_set_size(int64_t value) {

  size_ = value;
}
inline void TensorShapeProto_Dim::set_size(int64_t value) {
  _internal_set_size(value);
  // @@protoc_insertion_point(field_set:opencv_tensorflow.TensorShapeProto.Dim.size)
}

// string name = 2;
inline void TensorShapeProto_Dim::clear_name() {
  name_.ClearToEmpty();
}
inline const std::string& TensorShapeProto_Dim::name() const {
  // @@protoc_insertion_point(field_get:opencv_tensorflow.TensorShapeProto.Dim.name)
  return _internal_name();
}
template <typename ArgT0, typename... ArgT>
inline PROTOBUF_ALWAYS_INLINE
void TensorShapeProto_Dim::set_name(ArgT0&& arg0, ArgT... args) {

 name_.Set(::PROTOBUF_NAMESPACE_ID::internal::ArenaStringPtr::EmptyDefault{}, static_cast<ArgT0 &&>(arg0), args..., GetArenaForAllocation());
  // @@protoc_insertion_point(field_set:opencv_tensorflow.TensorShapeProto.Dim.name)
}
inline std::string* TensorShapeProto_Dim::mutable_name() {
  std::string* _s = _internal_mutable_name();
  // @@protoc_insertion_point(field_mutable:opencv_tensorflow.TensorShapeProto.Dim.name)
  return _s;
}
inline const std::string& TensorShapeProto_Dim::_internal_name() const {
  return name_.Get();
}
inline void TensorShapeProto_Dim::_internal_set_name(const std::string& value) {

  name_.Set(::PROTOBUF_NAMESPACE_ID::internal::ArenaStringPtr::EmptyDefault{}, value, GetArenaForAllocation());
}
inline std::string* TensorShapeProto_Dim::_internal_mutable_name() {

  return name_.Mutable(::PROTOBUF_NAMESPACE_ID::internal::ArenaStringPtr::EmptyDefault{}, GetArenaForAllocation());
}
inline std::string* TensorShapeProto_Dim::release_name() {
  // @@protoc_insertion_point(field_release:opencv_tensorflow.TensorShapeProto.Dim.name)
  return name_.Release(&::PROTOBUF_NAMESPACE_ID::internal::GetEmptyStringAlreadyInited(), GetArenaForAllocation());
}
inline void TensorShapeProto_Dim::set_allocated_name(std::string* name) {
  if (name != nullptr) {

  } else {

  }
  name_.SetAllocated(&::PROTOBUF_NAMESPACE_ID::internal::GetEmptyStringAlreadyInited(), name,
      GetArenaForAllocation());
#ifdef PROTOBUF_FORCE_COPY_DEFAULT_STRING
  if (name_.IsDefault(&::PROTOBUF_NAMESPACE_ID::internal::GetEmptyStringAlreadyInited())) {
    name_.Set(&::PROTOBUF_NAMESPACE_ID::internal::GetEmptyStringAlreadyInited(), "", GetArenaForAllocation());
  }
#endif // PROTOBUF_FORCE_COPY_DEFAULT_STRING
  // @@protoc_insertion_point(field_set_allocated:opencv_tensorflow.TensorShapeProto.Dim.name)
}

// -------------------------------------------------------------------

// TensorShapeProto

// repeated .opencv_tensorflow.TensorShapeProto.Dim dim = 2;
inline int TensorShapeProto::_internal_dim_size() const {
  return dim_.size();
}
inline int TensorShapeProto::dim_size() const {
  return _internal_dim_size();
}
inline void TensorShapeProto::clear_dim() {
  dim_.Clear();
}
inline ::opencv_tensorflow::TensorShapeProto_Dim* TensorShapeProto::mutable_dim(int index) {
  // @@protoc_insertion_point(field_mutable:opencv_tensorflow.TensorShapeProto.dim)
  return dim_.Mutable(index);
}
inline ::PROTOBUF_NAMESPACE_ID::RepeatedPtrField< ::opencv_tensorflow::TensorShapeProto_Dim >*
TensorShapeProto::mutable_dim() {
  // @@protoc_insertion_point(field_mutable_list:opencv_tensorflow.TensorShapeProto.dim)
  return &dim_;
}
inline const ::opencv_tensorflow::TensorShapeProto_Dim& TensorShapeProto::_internal_dim(int index) const {
  return dim_.Get(index);
}
inline const ::opencv_tensorflow::TensorShapeProto_Dim& TensorShapeProto::dim(int index) const {
  // @@protoc_insertion_point(field_get:opencv_tensorflow.TensorShapeProto.dim)
  return _internal_dim(index);
}
inline ::opencv_tensorflow::TensorShapeProto_Dim* TensorShapeProto::_internal_add_dim() {
  return dim_.Add();
}
inline ::opencv_tensorflow::TensorShapeProto_Dim* TensorShapeProto::add_dim() {
  ::opencv_tensorflow::TensorShapeProto_Dim* _add = _internal_add_dim();
  // @@protoc_insertion_point(field_add:opencv_tensorflow.TensorShapeProto.dim)
  return _add;
}
inline const ::PROTOBUF_NAMESPACE_ID::RepeatedPtrField< ::opencv_tensorflow::TensorShapeProto_Dim >&
TensorShapeProto::dim() const {
  // @@protoc_insertion_point(field_list:opencv_tensorflow.TensorShapeProto.dim)
  return dim_;
}

// bool unknown_rank = 3;
inline void TensorShapeProto::clear_unknown_rank() {
  unknown_rank_ = false;
}
inline bool TensorShapeProto::_internal_unknown_rank() const {
  return unknown_rank_;
}
inline bool TensorShapeProto::unknown_rank() const {
  // @@protoc_insertion_point(field_get:opencv_tensorflow.TensorShapeProto.unknown_rank)
  return _internal_unknown_rank();
}
inline void TensorShapeProto::_internal_set_unknown_rank(bool value) {

  unknown_rank_ = value;
}
inline void TensorShapeProto::set_unknown_rank(bool value) {
  _internal_set_unknown_rank(value);
  // @@protoc_insertion_point(field_set:opencv_tensorflow.TensorShapeProto.unknown_rank)
}

#ifdef __GNUC__
  #pragma GCC diagnostic pop
#endif  // __GNUC__
// -------------------------------------------------------------------


// @@protoc_insertion_point(namespace_scope)

}  // namespace opencv_tensorflow

// @@protoc_insertion_point(global_scope)

#include <google/protobuf/port_undef.inc>
#endif  // GOOGLE_PROTOBUF_INCLUDED_GOOGLE_PROTOBUF_INCLUDED_tensor_5fshape_2eproto
