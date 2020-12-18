# %%
import sys
import matplotlib.pyplot as plt
from pathlib import Path
from typing import List, Dict, Tuple
import numpy as np
import pandas as pd
from PIL import Image
import math
import os
import random

import tensorflow as tf
from tensorflow.keras.preprocessing import image_dataset_from_directory
import cv2

print("Num GPUs Available: ", len(tf.config.experimental.list_physical_devices('GPU')))

DATASET_PATH = Path("./dataset/human_parsing/")

TRAIN_IDX = DATASET_PATH / "TrainVal_images" / "train_id.txt"
TRAIN_NAME = []
with open(TRAIN_IDX, "r") as f:
    for line in f:
        TRAIN_NAME.append(line.strip())

VAL_IDX = DATASET_PATH / "TrainVal_images" / "val_id.txt"
VAL_NAME = []
with open(VAL_IDX, "r") as f:
    for line in f:
        VAL_NAME.append(line.strip())

INPUT_EXT = ".jpg"
PARSING_EXT = ".png"

TRAIN_INPUT_PATH = DATASET_PATH / "TrainVal_images" / "TrainVal_images" / "train_images"
VAL_INPUT_PATH = DATASET_PATH / "TrainVal_images" / "TrainVal_images" / "val_images"

TRAIN_PARSING_PATH = DATASET_PATH / \
    "TrainVal_parsing_annotations" / \
    "TrainVal_parsing_annotations" / \
    "train_segmentations"
VAL_PARSING_PATH = DATASET_PATH / \
    "TrainVal_parsing_annotations" / \
    "TrainVal_parsing_annotations" / \
    "val_segmentations"

LABEL_NAME = {
    'Hat': 1,
    'Hair': 2,
    'Glove': 3,
    'Sunglasses': 4,
    'UpperClothes': 5,
    'Dress': 6,
    'Coat': 7,
    'Socks': 8,
    'Pants': 9,
    'Jumpsuits': 10,
    'Scarf': 11,
    'Skirt': 12,
    'Face': 13,
    'Left-arm': 14,
    'Right-arm': 15,
    'Left-leg': 16,
    'Right-leg': 17,
    'Left-shoe': 18,
    'Right-shoe': 19
}

def show_img(img):
    plt.figure()
    plt.imshow(img)
    plt.show()


# %%

r = np.random.randint(0, 5000)

TEST_INPUT = np.asarray(Image.open(TRAIN_INPUT_PATH / (TRAIN_NAME[r] + INPUT_EXT)))
TEST_LABEL = np.asarray(Image.open(TRAIN_PARSING_PATH / (TRAIN_NAME[r] + PARSING_EXT)))

print(f"Shape: {TEST_INPUT.shape}")
show_img(TEST_INPUT)
show_img(TEST_LABEL)

# %%



# %%

def train_gen():
    for file_name in TRAIN_NAME:
        img = tf.convert_to_tensor(
            np.asarray(
                Image.open(
                    TRAIN_INPUT_PATH / (file_name + INPUT_EXT)
                )
            ),
            dtype=tf.float32)
        if len(img.shape) != 3:
            continue
        resized_img = tf.keras.layers.experimental.preprocessing.Resizing(300, 300)(img)
        rescaled_img = tf.keras.layers.experimental.preprocessing.Rescaling(scale=1./255)(resized_img)
        label = \
            np.expand_dims(
                np.asarray(
                    Image.open(
                        TRAIN_PARSING_PATH / (file_name + PARSING_EXT)
                    ),
                    dtype=float
                ), axis=2
            )
        if len(label.shape) != 3:
            continue
        label[label > 0] = 1
        label = tf.convert_to_tensor(label, dtype=tf.float32)
        resized_label = tf.keras.layers.experimental.preprocessing.Resizing(300, 300)(label)
        yield rescaled_img, resized_label

# Tensorflow dataset object
ds = tf.data.Dataset.from_generator(train_gen, output_signature=(
    tf.TensorSpec(shape=(300, 300, 3), dtype=tf.float32),
    tf.TensorSpec(shape=(300, 300, 1), dtype=tf.float32)
))

# Generator object
train_gen_obs = train_gen()

batchs = ds.repeat().batch(20)
batchs
# %%

for a, b in batchs:
    print(b.shape)

# %%


label = np.expand_dims(
    np.asarray(
        Image.open(
            TRAIN_PARSING_PATH / (TRAIN_NAME[4] + PARSING_EXT)
        ),
        dtype=float
    ),
    axis=2
)
sh = label.shape
print(sh)
show_img(label)
label[label > 0] = 1
print(label.shape)
show_img(label)
label = tf.convert_to_tensor(label, dtype=tf.int32)
show_img(label)

# %%

# a, b = next(train_gen_obs)
a, b = next(iter(ds))
# show_img(a)
# show_img(b)

# %%

# Build a simple Convolutional Autoencoder model

inputs = tf.keras.layers.Input(shape=(300, 300, 3))
x = tf.keras.layers.Conv2D(
    filters=40, 
    kernel_size=(3, 3), 
    activation='relu', 
    padding='same'
) (inputs)
x = tf.keras.layers.MaxPooling2D(
    pool_size=(2, 2),
    padding='same'
) (x)

x = tf.keras.layers.Conv2D(
    filters=20, 
    kernel_size=(3, 3), 
    activation='relu', 
    padding='same') (x)
x = tf.keras.layers.MaxPooling2D(
    pool_size=(2, 2),
    padding='same'
) (x)


x = tf.keras.layers.Conv2D(
    filters=20, 
    kernel_size=(3, 3), 
    activation='relu', 
    padding='same') (x)
x = tf.keras.layers.UpSampling2D(
    (2, 2)
) (x)


x = tf.keras.layers.Conv2D(
    filters=40, 
    kernel_size=(3, 3), 
    activation='relu', 
    padding='same') (x)
x = tf.keras.layers.UpSampling2D(
    (2, 2)
) (x)

outputs = tf.keras.layers.Conv2D(
    filters=1, 
    kernel_size=(3, 3), 
    activation='sigmoid',
    padding='same') (x)


model = tf.keras.Model(inputs, outputs)
model.compile(loss='binary_crossentropy', metrics=['acc'])
model.summary()

# %%
with tf.device("/GPU:0"):
    history = model.fit(batchs, steps_per_epoch=20, epochs=50)


# %%

r = np.random.randint(0, 5000)
test_o = tf.convert_to_tensor(np.asarray(Image.open(TRAIN_INPUT_PATH / (TRAIN_NAME[r] + INPUT_EXT))))
test_o = tf.keras.layers.experimental.preprocessing.Resizing(300, 300)(test_o)
test_o = tf.keras.layers.experimental.preprocessing.Rescaling(scale=1./255)(test_o)

show_img(test_o)

test_a = model.predict(tf.expand_dims(test_o, axis=0))
show_img(np.asarray(test_a[0]))

ref = test_a[0] > 0.6

show_img(ref)

# %%

lip_test = "./dataset/lip_mpv_dataset/MPV_192_256/0VB21E007/0VB21E007-T11@8=person_half_front.jpg"
test_o = tf.convert_to_tensor(np.asarray(Image.open(lip_test)))
test_o = tf.keras.layers.experimental.preprocessing.Resizing(300, 300)(test_o)
test_o = tf.keras.layers.experimental.preprocessing.Rescaling(scale=1./255)(test_o)

show_img(test_o)

test_a = model.predict(tf.expand_dims(test_o, axis=0))
show_img(np.asarray(test_a[0]))

ref = test_a[0] > 0.64

show_img(ref)
# %%%
test_o

# %%

def count(stop):
    i = 0
    while i<stop:
        yield i
        i += 1

for n in count(5):
    print(n)

ds_counter = tf.data.Dataset.from_generator(count, args=[25],
    output_types=tf.int32, output_shapes = ()) 

# %%
cnt = count(6)
# %%
for count_batch in ds_counter.repeat().batch(10).take(10):
    print(count_batch.numpy())
