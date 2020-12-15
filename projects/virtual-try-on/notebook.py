# %%

import sys
import matplotlib.pyplot as plt
from pathlib import Path
from typing import List, Dict, Tuple
import numpy as np
import pandas as pd
from PIL import Image
import math

import tensorflow as tf
from tensorflow.keras.preprocessing import image_dataset_from_directory
import cv2
# CONFIG
DATASET_PATH = Path("./dataset/yalefaces")

TEST_FILE_NAME = "subject01.happy"
TEST_FILE_PATH = DATASET_PATH / TEST_FILE_NAME

TEST_FILE_NAME_2 = "subject01.sad"
TEST_FILE_PATH_2 = DATASET_PATH / TEST_FILE_NAME_2

TEST_FILE_NAME_3 = "subject04.sad"
TEST_FILE_PATH_3 = DATASET_PATH / TEST_FILE_NAME_3

TEST_FILE_NAME_4 = "subject04.happy"
TEST_FILE_PATH_4 = DATASET_PATH / TEST_FILE_NAME_4

BATCH_SIZE = 16
IMG_SIZE = (243, 320)

def draw_a_random_box(img: np.ndarray, size: int=40) -> np.ndarray:
    """[summary]

    Args:
        img (np.ndarray): [description]
        size (int): [description]

    Returns:
        np.ndarray: [description]
    """

    i = np.random.randint(0, img.shape[0] - 1 - size)
    j = np.random.randint(0, img.shape[1] - 1 - size)
    new_img = img.copy()
    new_img[i:i+size, j:j+size] = 1
    return new_img

def show_img(img: np.ndarray):
    """ Show the image in a gray scale manner

    Args:
        img (np.ndarray): the numpy representation of the matrix.
    """
    plt.figure()
    plt.imshow(img, cmap=plt.cm.gray)
    plt.show()

def convert_to_RGB(img: np.ndarray):
    return cv2.cvtColor(img1, cv2.COLOR_BGR2RGB)


# %%

img1 = np.asarray(Image.open(TEST_FILE_PATH))
img2 = np.asarray(Image.open(TEST_FILE_PATH))
new_img1 = convert_to_RGB(img1)
plt.figure()
plt.imshow(new_img1)
plt.show()
print(f"image shape: {new_img1.shape}")


# %%

train_dataset = image_dataset_from_directory(DATASET_PATH,
                                             shuffle=True,
                                             batch_size=BATCH_SIZE,
                                             image_size=IMG_SIZE)



# %%


data_augmentation = tf.keras.Sequential([
  tf.keras.layers.experimental.preprocessing.RandomFlip('horizontal'),
  tf.keras.layers.experimental.preprocessing.RandomRotation(0.2),
])
preprocess_input = tf.keras.applications.mobilenet_v2.preprocess_input

IMG_SHAPE = new_img1.shape
base_model = tf.keras.applications.MobileNetV2(input_shape=IMG_SHAPE,
                                               include_top=False,
                                               weights='imagenet')

# %%


new_img1.shape
# %%

# in_tensor, label_batch = np.asarray([new_img1]), [new_img1]
inputs = tf.keras.Input(shape=IMG_SHAPE)
x = base_model(inputs)
print(x.shape)

global_average_layer = tf.keras.layers.GlobalAveragePooling2D()
x = global_average_layer(x)

print(x.shape)

reshape_layer = tf.keras.layers.Reshape((1,1,-1))
x = reshape_layer(x)

print(x.shape)

upsample_layer = tf.keras.layers.UpSampling2D()
x = upsample_layer(x)

upsample_layer = tf.keras.layers.UpSampling2D()
x = upsample_layer(x)

upsample_layer = tf.keras.layers.UpSampling2D()
x = upsample_layer(x)

global_average_layer = tf.keras.layers.GlobalAveragePooling2D()
x = global_average_layer(x)

mul = 243 * 320 * 3

prediction_layer = tf.keras.layers.Dense(mul)
x = prediction_layer(x)

reshape_layer = tf.keras.layers.Reshape(IMG_SHAPE)
out = reshape_layer(x)

print(out.shape)

model = tf.keras.Model(inputs, out)

# %%

model.summary()

# %%

loss0, accuracy0 = model.evaluate(validation_dataset)


# %% 
import os
for file_name in os.listdir(DATASET_PATH):
    