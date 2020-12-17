
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

import tensorflow as tf
from tensorflow.keras.preprocessing import image_dataset_from_directory
import cv2
# CONFIG
DATASET_PATH = Path("./dataset/yalefaces")

TEST_FILE_NAME = "subject01.happy.jpg"
TEST_FILE_PATH = DATASET_PATH / TEST_FILE_NAME

TEST_FILE_NAME_2 = "subject01.sad.jpg"
TEST_FILE_PATH_2 = DATASET_PATH / TEST_FILE_NAME_2

TEST_FILE_NAME_3 = "subject04.sad.jpg"
TEST_FILE_PATH_3 = DATASET_PATH / TEST_FILE_NAME_3

TEST_FILE_NAME_4 = "subject04.happy.jpg"
TEST_FILE_PATH_4 = DATASET_PATH / TEST_FILE_NAME_4

BATCH_SIZE = 16
IMG_SIZE = (243, 320, 3)

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
    new_img[i:i+size, j:j+size, :] = 0
    return new_img

def show_img(img: np.ndarray):
    """ Show the image in a gray scale manner

    Args:
        img (np.ndarray): the numpy representation of the matrix.
    """
    if len(img.shape) == 2:
        plt.figure()
        plt.imshow(img, cmap=plt.cm.gray)
        plt.show()
    elif len(img.shape) == 3:
        plt.figure()
        plt.imshow(img)
        plt.show()
    else:
        print("Type not supported")

def convert_to_RGB(img: np.ndarray):
    return cv2.cvtColor(img, cv2.COLOR_BGR2RGB)

def rescale(img: np.ndarray):
    return img * 1.0/255

# %%

img1 = np.asarray(Image.open(TEST_FILE_PATH))
img2 = np.asarray(Image.open(TEST_FILE_PATH))
new_img1 = rescale(convert_to_RGB(img1))
plt.figure()
plt.imshow(new_img1)
plt.show()
print(f"image shape: {new_img1.shape}")

# %%

# read data form folder

label = np.empty(shape=(0, 243, 320, 3))

for file_name in os.listdir(DATASET_PATH):
    img = rescale(convert_to_RGB(np.asarray(Image.open(DATASET_PATH / file_name))))
    label = np.concatenate((label, img.reshape(1, *img.shape)), axis=0)

train = np.empty(shape=(0,243, 320, 3))

for image in label:
    img = draw_a_random_box(image)
    train = np.concatenate((train, img.reshape(1, *img.shape)), axis=0)

print(train.shape)

ds = tf.data.Dataset.from_tensor_slices((train, label))
print(ds.element_spec)


train_img, label_img = next(iter(ds))
print(train_img)


print("Train img:")
print(f"Shape: {train_img.shape}")
show_img(train_img)
print("Label img:")
print(f"Shape: {label_img.shape}")
show_img(label_img)
# %%


# in_tensor, label_batch = np.asarray([new_img1]), [new_img1]
class AutoEncoder(tf.keras.models.Model):
    def __init__(self, latent_dim, img_shape=(243, 320, 3)):
        super().__init__()
        self.latent_dim = latent_dim
        self.encoder = tf.keras.Sequential([
            tf.keras.layers.Flatten(),
            tf.keras.layers.Dense(latent_dim, activation='relu')
        ])
        self.decoder = tf.keras.Sequential([
            tf.keras.layers.Dense(233280, activation='sigmoid'),
            tf.keras.layers.Reshape(img_shape)
        ])

    def call(self, x):
        encoded_tensor = self.encoder(x)
        print(encoded_tensor.shape)
        decoded_tensor = self.decoder(encoded_tensor)
        print(decoded_tensor.shape)
        return decoded_tensor

autoencoder = AutoEncoder(512)
autoencoder.compile(loss='mse', metrics=['accuracy'])

autoencoder.build(input_shape=(1, *IMG_SIZE))
autoencoder.summary()
#%%


# %%

# loss0, accuracy0 = autoencoder.evaluate(ds)
history = autoencoder.fit(train, label, epochs=4)

# %% 

img = autoencoder.predict(train[0:1])[0]

print("Original:")
plt.figure()
plt.imshow(train[0])
plt.show()

print("Predicted:")
plt.figure()
plt.imshow(img)
plt.show()

# %%

in_tensor = tf.keras.layers.Input(shape=IMG_SHAPE)

encoder = tf.keras.Sequential([
    tf.keras.layers.Flatten(),
    tf.keras.layers.Dense(512, activation='relu')
])
decoder = tf.keras.Sequential([
    tf.keras.layers.Dense(233280, activation='sigmoid'),
    tf.keras.layers.Reshape(IMG_SHAPE)
])

tensor = encoder(in_tensor)
output = decoder(tensor)

model = tf.keras.Model(in_tensor, output)
model.compile(loss='mse', metrics=['accuracy'])
model.summary()


# %%

history = model.fit(dataset, epochs=2)

# %%

plt.figure()
plt.plot(history.history['loss'])

# %%

img = model.predict(train[0:1])[0]

print("Original:")
plt.figure()
plt.imshow(train[0])
plt.show()

print("Predicted:")
plt.figure()
plt.imshow(img)
plt.show()

# %%

some_img = autoencoder.encoder.layers[1].weights[0]

show_img(some_img.numpy())
