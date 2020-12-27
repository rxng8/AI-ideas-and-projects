import sys
import matplotlib.pyplot as plt
from pathlib import Path
from typing import List, Dict, Tuple
import numpy as np
import pandas as pd
from PIL import Image
import math
import os
import re

import tensorflow as tf
from tensorflow.keras.preprocessing import image_dataset_from_directory
import cv2

import mediapipe as mp
mp_drawing = mp.solutions.drawing_utils
mp_pose = mp.solutions.pose
mp_holistic = mp.solutions.holistic
mp_hands = mp.solutions.hands

IMG_SHAPE = (256, 192, 3)
LABEL_NAME = {
    'Background': 0,
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


def conv(batch_input, out_channels, strides=1, activation='relu'):

    # padded_input = tf.pad(
    #     batch_input, 
    #     [[0, 0], [1, 1], [1, 1], [0, 0]], 
    #     mode="CONSTANT"
    # )

    out = tf.keras.layers.Conv2D(
        filters=out_channels, 
        kernel_size=(4, 4),
        activation=activation, 
        padding='same'
    )(batch_input)
    # print(out.shape)
    return out

def dropout(batch_input, rate=0.5):
    return tf.keras.layers.Dropout(
        rate
    ) (batch_input)

def max_pool(batch_input):
    return tf.keras.layers.MaxPooling2D(
        pool_size=(2, 2),
        padding='same'
    ) (batch_input)

def final_conv(batch_input, out_channels):
    # out = tf.keras.layers.Conv2D(
    #     filters=out_channels, 
    #     kernel_size=(4, 4),
    #     activation='sigmoid', 
    #     padding='same'
    # )(batch_input)
    # return out
    return conv(batch_input, out_channels, activation='tanh')

def deconv(batch_input, out_channels, activation='relu'):
    return tf.keras.layers.Conv2DTranspose(
        out_channels, 
        4, 
        strides=2,
        padding='same',
        activation=activation
    ) (batch_input)

def final_deconv(batch_input, out_channels):
    return deconv(batch_input, out_channels, activation='sigmoid')

def preprocess_image(
        img: np.ndarray, 
        shape=(256, 192),
        resize_method=tf.image.ResizeMethod.BILINEAR
    ) -> tf.Tensor:
    # Expect range 0 - 255
    resized = tf.image.resize(
        img, 
        shape,
        method=resize_method
    )
    rescaled = tf.cast(resized, dtype=float) / 255.0
    rescaled = (rescaled - 0.5) * 2 # range [-1, 1]
    # Convert to BGR
    bgr = rescaled[..., ::-1]
    return bgr

def deprocess_img(img):
    # Expect img range [-1, 1]
    # Do the rescale back to 0, 1 range, and convert from bgr back to rgb
    return (img / 2.0 + 0.5)[..., ::-1]

def show_img(img):
    if len(img.shape) == 3 and img.shape[2] == 3:
        plt.figure()
        plt.imshow(img)
        plt.axis('off')
        plt.show()
    elif len(img.shape) == 2 or img.shape[2] == 1:
        plt.figure()
        plt.imshow(img, cmap='gray')
        plt.axis('off')
        plt.show()

def load_image(path):
    return np.asarray(Image.open(path))


def compute_mae_loss(real, pred):
    return tf.reduce_mean(tf.math.abs(real - pred))

def compute_mse_loss(real, pred):
    return tf.reduce_mean(tf.math.abs(real - pred) ** 2)