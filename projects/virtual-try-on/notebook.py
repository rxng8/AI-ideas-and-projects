# %%
import sys
import matplotlib.pyplot as plt
from pathlib import Path
from typing import List, Dict, Tuple
import numpy as np
import pywt
import pywt.data
from PIL import Image
import math

# import matplotlib.pylab as plt
from skimage.io import imread
from skimage.color import rgb2gray
from skimage import filters

# CONFIG
DATASET_PATH = Path("./dataset/yalefaces")

# Test file paths. Note that the first two face is the same person,
# and the next two face is also the same person.
TEST_FILE_NAME = "subject01.happy"
TEST_FILE_PATH = DATASET_PATH / TEST_FILE_NAME

TEST_FILE_NAME_2 = "subject01.sad"
TEST_FILE_PATH_2 = DATASET_PATH / TEST_FILE_NAME_2

TEST_FILE_NAME_3 = "subject04.sad"
TEST_FILE_PATH_3 = DATASET_PATH / TEST_FILE_NAME_3

TEST_FILE_NAME_4 = "subject04.happy"
TEST_FILE_PATH_4 = DATASET_PATH / TEST_FILE_NAME_4


# %%




