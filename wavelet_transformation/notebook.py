# %%
import sys
import matplotlib.pyplot as plt
from pathlib import Path
from typing import List, Dict, Tuple
import numpy as np
import pywt
import pywt.data
from PIL import Image

# CONFIG
DATASET_PATH = Path("./dataset/yalefaces")

TEST_FILE_NAME = "subject01.happy"
TEST_FILE_PATH = DATASET_PATH / TEST_FILE_NAME

def dwt(img: np.ndarray) -> Tuple[np.ndarray]:
    """ Perform discrete wavelet transform on the image.

    Args:
        img (np.ndarray): The matrix representation of the image.

    Returns:
        Tuple[np.ndarray]: Return lowpass and highpass portions combination.
    """
    coeffs2 = pywt.dwt2(img, 'bior1.3')
    LL, (LH, HL, HH) = coeffs2
    return LL, LH, HL, HH

def draw_horizontal_baselines(img: np.ndarray, 
                    baselines: List[int], 
                    on_img: np.ndarray=None) -> np.ndarray:
    """ Draw the horizontal baselines on the image by setting all value to 0.

    Args:
        img (np.ndarray): shape(height, width)
        baselines (List[int]): list of baselines (rows)
        on_img: np.ndarray: The image that the user want to draw 
            the baseline on, if it is None, then the original image is
            copied and then drawn baseline on that image. Default to None.

    Returns:
        np.ndarray: The image with the horizontal baseline.
    """
    drawn = None
    if on_img is not None:
        assert on_img.shape == img.shape, "The two image should have the same dimension."
        drawn = on_img.copy()
    else:
        drawn = img.copy()
    for baseline in baselines:
        drawn[baseline,:] = 1
    return drawn

def draw_vertical_baselines(img: np.ndarray, 
                    baselines: List[int], 
                    on_img: np.ndarray=None) -> np.ndarray:
    """ Draw the vertical baselines on the image by setting all value to 0.

    Args:
        img (np.ndarray): shape(height, width)
        baselines (List[int]): list of baselines (rows)
        on_img: np.ndarray: The image that the user want to draw 
            the baseline on, if it is None, then the original image is
            copied and then drawn baseline on that image. Default to None.

    Returns:
        np.ndarray: The image with the vertical baseline.
    """
    drawn = None
    if on_img is not None:
        assert on_img.shape == img.shape, "The two image should have the same dimension."
        drawn = on_img.copy()
    else:
        drawn = img.copy()
    for baseline in baselines:
        drawn[:,baseline] = 1
    return drawn

def get_max_value_indices(values: np.ndarray, n: int=3) -> List[int]:
    """ Get the indices of the max value in the array

    Args:
        values (np.ndarray): Shape (-1,). 1D array
        n (int, optional): The number of indices. Defaults to 3.

    Returns:
        List[int]: The indices of n max value in the values array.
    """
    return (-values).argsort()[:n]

def horizontal_projection(img: np.ndarray) -> np.ndarray:
    """ Compute the horizontal projection of an image

    Args:
        img (np.ndarray): 2d array. Shape(height, width)

    Returns:
        np.ndarray: The 1d matrix representing the horizontal projection
            of that vector.
    """
    assert len(img.shape) == 2, "Wrong behavior due to wrong image dimension."
    return np.sum(img, axis=0)

def vertical_projection(img: np.ndarray) -> np.ndarray:
    """ Compute the vertical projection of an image

    Args:
        img (np.ndarray): 2d array. Shape(height, width)

    Returns:
        np.ndarray: The 1d matrix representing the vertical projection
            of that vector.
    """
    assert len(img.shape) == 2, "Wrong behavior due to wrong image dimension."
    return np.sum(img, axis=1)

def show_img(img: np.ndarray):
    plt.imshow(img, cmap=plt.cm.gray)

# %%

# Load image
original = pywt.data.camera()

# Wavelet transform of image, and plot approximation and details
titles = ['Approximation', ' Horizontal detail',
          'Vertical detail', 'Diagonal detail']
coeffs2 = pywt.dwt2(original, 'bior1.3')
LL, (LH, HL, HH) = coeffs2
fig = plt.figure(figsize=(12, 3))
for i, a in enumerate([LL, LH, HL, HH]):
    ax = fig.add_subplot(1, 4, i + 1)
    ax.imshow(a, interpolation="nearest", cmap=plt.cm.gray)
    ax.set_title(titles[i], fontsize=10)
    ax.set_xticks([])
    ax.set_yticks([])

fig.tight_layout()
plt.show()

# %%


plt.imshow(LL, cmap=plt.cm.gray)

# %%


LL1, LH1, HL1, HH1 = dwt(LL)

# %%

show_img(LH1)

# %%
vp = vertical_projection(HH1)
ids = get_max_value_indices(vp)
new_LL1 = draw_horizontal_baselines(HH1, ids, LL1)
show_img(new_LL1)

# %%

# Read image 
img: np.ndarray = np.asarray(Image.open(TEST_FILE_PATH))
print("original image:")
show_img(img)
# %%
LL, LH, HL, HH = dwt(img)
show_img(HH)

# %%
LL1, LH1, HL1, HH1 = dwt(LL)
show_img(LH1)

# %%
vp = vertical_projection(HH1)
ids = get_max_value_indices(vp, 4)
new_LL1 = draw_horizontal_baselines(HH1, ids, LL1)
show_img(new_LL1)

# %%

new_img = img - np.append(img[:,-1:], img[:,:-1], axis=1)

show_img(new_img)
# %%

img.shape

# %%

import matplotlib.pylab as plt
from skimage.io import imread
from skimage.color import rgb2gray
from skimage import filters
im = img# RGB image to gray scale
plt.gray()
plt.figure(figsize=(20,20))
plt.subplot(221)
plt.imshow(im)
plt.title('original', size=20)
plt.subplot(222)
edges_y = filters.sobel_h(im) 
plt.imshow(edges_y)
plt.title('sobel_x', size=20)
plt.subplot(223)
edges_x = filters.sobel_v(im)
plt.imshow(edges_x)
plt.title('sobel_y', size=20)
plt.subplot(224)
edges = filters.sobel(im)
plt.imshow(edges)
plt.title('sobel', size=20)
plt.show()

# %%
# edges = edges[3:-3,:]
hp = horizontal_projection(edges)
# ids = get_max_value_indices(vp, 4)
# new_LL1 = draw_horizontal_baselines(HH1, ids, LL1)
# show_img(new_LL1)
# idx = []
# cur_data = None
# for i, data in enumerate(hp):
#     if cur_data is not None:
#         change = data - cur_data

# %%

mid = edges.shape[0] // 2
left_bound_id = -1
for i, data in enumerate(edges[mid]):
    if data > 0:
        left_bound_id = i
        break

right_bound_id = -1
for i in range(len(edges[0]) - 1, -1, -1):
    if edges[mid, i]:
        right_bound_id = i
        break

# %%%

new_edges = draw_vertical_baselines(edges, [left_bound_id, right_bound_id], img)
show_img(new_edges)



# %%


vp = vertical_projection(edges[3:-3, :])

ids = []
s = set()
n = 3
range_threshold = 20
i = 0 # traverse index in the soreted idx
sorted_idx = (-vp).argsort()
while n > 0 and i < len(sorted_idx):
    if sorted_idx[i] not in s:
        ids.append(sorted_idx[i])
        s.add(sorted_idx[i])
        for k in range(range_threshold):
            s.add(sorted_idx[i] - k)
            s.add(sorted_idx[i] + k)
        n -= 1
    i += 1

new_edges = draw_horizontal_baselines(edges, ids, new_edges)
show_img(new_edges)









# %%


img: np.ndarray = np.asarray(Image.open(TEST_FILE_PATH))
print("original image:")
show_img(img)
LL, LH, HL, HH = dwt(img)
show_img(HL)

# %%
hp = horizontal_projection(HL)
ids = get_max_value_indices(hp, 10)
new_LL = draw_vertical_baselines(HL, ids)
show_img(new_LL)




