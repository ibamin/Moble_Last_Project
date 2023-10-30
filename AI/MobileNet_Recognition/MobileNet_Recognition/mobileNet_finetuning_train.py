from keras.applications import MobileNet

# MobileNet was designed to work on 224 x 224 pixel input images sizes
img_rows, img_cols = 224, 224

# Re-loads the MobileNet model without the top or FC layers
MobileNet = MobileNet(
    weights="imagenet", include_top=False, input_shape=(img_rows, img_cols, 3)
)

# Here we freeze the last 4 layers
# Layers are set to trainable as True by default
for layer in MobileNet.layers:
    layer.trainable = False

# Let's print our layers
for i, layer in enumerate(MobileNet.layers):
    print(str(i) + " " + layer.__class__.__name__, layer.trainable)


def lw(bottom_model, num_classes):
    """creates the top or head of the model that will be
    placed ontop of the bottom layers"""

    top_model = bottom_model.output
    top_model = GlobalAveragePooling2D()(top_model)
    top_model = Dense(1024, activation="relu")(top_model)
    top_model = Dense(1024, activation="relu")(top_model)
    top_model = Dense(512, activation="relu")(top_model)
    top_model = Dense(num_classes, activation="softmax")(top_model)
    return top_model


from keras.models import Sequential
from keras.layers import Dense, Dropout, Activation, Flatten, GlobalAveragePooling2D
from keras.layers import Conv2D, MaxPooling2D, ZeroPadding2D
from keras.models import Model

# Set our class number to 4
num_classes = 4

FC_Head = lw(MobileNet, num_classes)

model = Model(inputs=MobileNet.input, outputs=FC_Head)

print(model.summary())

from keras.preprocessing.image import ImageDataGenerator

train_data_dir = "MobileNet_Recognition/MobileNet_Recognition/train/"
validation_data_dir = "MobileNet_Recognition/MobileNet_Recognition/val/"

# Let's use some data augmentaiton
train_datagen = ImageDataGenerator(
    rescale=1.0 / 255,
    rotation_range=45,
    width_shift_range=0.3,
    height_shift_range=0.3,
    horizontal_flip=True,
    fill_mode="nearest",
)

validation_datagen = ImageDataGenerator(rescale=1.0 / 255)

# set our batch size (typically on most mid tier systems we'll use 16-32)
batch_size = 32

train_generator = train_datagen.flow_from_directory(
    train_data_dir,
    target_size=(img_rows, img_cols),
    batch_size=batch_size,
    class_mode="categorical",
)

validation_generator = validation_datagen.flow_from_directory(
    validation_data_dir,
    target_size=(img_rows, img_cols),
    batch_size=batch_size,
    class_mode="categorical",
)

from keras.optimizers import RMSprop
from keras.callbacks import ModelCheckpoint, EarlyStopping


checkpoint = ModelCheckpoint(
    "MobileNet_Recognition/MobileNet_Recognition/face.h5",
    monitor="val_loss",
    mode="min",
    save_best_only=True,
    verbose=1,
)

earlystop = EarlyStopping(
    monitor="val_loss", min_delta=0, patience=3, verbose=1, restore_best_weights=True
)

# we put our call backs into a callback list
callbacks = [earlystop, checkpoint]

# We use a very small learning rate
model.compile(
    loss="categorical_crossentropy", optimizer=RMSprop(lr=0.001), metrics=["accuracy"]
)

# Enter the number of training and validation samples here
nb_train_samples = 237
nb_validation_samples = 25

# We only train 5 EPOCHS
epochs = 5
batch_size = 5

history = model.fit_generator(
    train_generator,
    steps_per_epoch=nb_train_samples // batch_size,
    epochs=epochs,
    callbacks=callbacks,
    validation_data=validation_generator,
    validation_steps=nb_validation_samples // batch_size,
)
