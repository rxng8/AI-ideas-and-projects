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

import mediapipe as mp

# config

DATASET_PATH = Path("./dataset/lip_mpv_dataset/")
DATASET_SRC = DATASET_PATH / "MPV_192_256"
DATASET_FILE = "all_poseA_poseB_clothes.txt"

def get_data_path():
    train_half_front = []
    test_half_front = []

    with open(DATASET_PATH / DATASET_FILE, 'r') as f:
        for line in f:
            elems = line.split("\t")
            assert len(elems) == 4, "Unexpected readline!"
            if "train" in line:
                if "person_half_front.jpg" in line and "cloth_front.jpg" in line:
                    tmp_person = ""
                    tmp_cloth = ""
                    for elem in elems:
                        if "person_half_front.jpg" in elem:
                            tmp_person = str(DATASET_SRC / elem)
                        if "cloth_front.jpg" in elem:
                            tmp_cloth = str(DATASET_SRC / elem)
                    train_half_front.append([tmp_person, tmp_cloth])
                else:
                    continue
            elif "test" in line:
                if "person_half_front.jpg" in line and "cloth_front.jpg" in line:
                    tmp_person = ""
                    tmp_cloth = ""
                    for elem in elems:
                        if "person_half_front.jpg" in elem:
                            tmp_person = str(DATASET_SRC / elem)
                        if "cloth_front.jpg" in elem:
                            tmp_cloth = str(DATASET_SRC / elem)
                    test_half_front.append([tmp_person, tmp_cloth])
                else:
                    continue
            else:
                print("Unexpected behavior!")

    return np.asarray(train_half_front), np.asarray(test_half_front)

def get_pose_map_generator(path_list: np.ndarray) -> None:
    """ given a path, return a pose map

    Args:
        img (np.ndarray): 

    Returns:
        np.ndarray: [description]
    """
    mp_drawing = mp.solutions.drawing_utils
    mp_pose = mp.solutions.pose
    mp_holistic = mp.solutions.holistic
    pose = mp_pose.Pose(
        static_image_mode=True, min_detection_confidence=0.5)
    for idx, f in enumerate(path_list):
        image = cv2.imread(f)
        image_height, image_width, n_channels = image.shape
        # Convert the BGR image to RGB before processing.
        results = pose.process(cv2.cvtColor(image, cv2.COLOR_BGR2RGB))
        if not results.pose_landmarks:
            continue
        # Draw pose landmarks on the image.
        annotated_image = np.zeros(shape=(image_height, image_width, n_channels))
        mp_drawing.draw_landmarks(
            annotated_image, results.pose_landmarks)
        yield np.asarray(annotated_image) / 255.0
    pose.close()

def get_pose_map(path: np.ndarray) -> np.ndarray:
    """ given a path, return a pose map

    Args:
        img (np.ndarray): 

    Returns:
        np.ndarray: [description]
    """
    mp_drawing = mp.solutions.drawing_utils
    mp_pose = mp.solutions.pose
    mp_holistic = mp.solutions.holistic
    pose = mp_pose.Pose(
    static_image_mode=True, min_detection_confidence=0.5)
    image = cv2.imread(path)
    image_height, image_width, n_channels = image.shape
    # Convert the BGR image to RGB before processing.
    results = pose.process(cv2.cvtColor(image, cv2.COLOR_BGR2RGB))

    if results.pose_landmarks:
        annotated_image = np.zeros(shape=(image_height, image_width, n_channels))
        mp_drawing.draw_landmarks(
            annotated_image, results.pose_landmarks)
        pose.close()
        return np.asarray(annotated_image) / 255.0
    pose.close()


def generator(dataset_path: str, file_path_list: np.ndarray):
    pass

# %%


train_path, test_path = get_data_path()
gen = get_pose_map_generator(train_path[:,0])
img = np.asarray(next(gen))
plt.imshow(img / 255.0)

# %%



# %%

# Personal representation:
#   - Pose heatmap (18 channels) Check (3 channels)
#   - Human segmentation (1 channel)
#   - Face and hair segmentation (3 channels)
# 




