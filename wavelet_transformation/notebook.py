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

def show_wavelet_decomposition(img: np.ndarray) -> None:
    """ Draw the graph of the wavelet 2-level decomposition.

    Args:
        img (np.ndarray): The numpy representation of the image.
    """
    level_1_transform = list(dwt(img)) # This method will return LL, LH, HL, HH
    level_2_transform = []
    for f in level_1_transform:
        # concatenate new tranformed feature images for each transformed portion
        # in level 1 transform.
        level_2_transform = [*level_2_transform, *list(dwt(f))]

    fig = plt.figure(figsize=(12, 12))
    # For each feature image in the level 2 transform.
    for i, a in enumerate(level_2_transform):
        ax = fig.add_subplot(4, 4, i + 1)
        ax.imshow(a, interpolation="nearest", cmap=plt.cm.gray)
        ax.set_xticks([])
        ax.set_yticks([])

    fig.tight_layout()
    plt.show(block=True)

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

def draw_bounding_box(img: np.ndarray, bounding_box: Tuple[Tuple[int]]) -> np.ndarray:
    """ Return a copy of the image with the bounding box drawn.

    Args:
        img ([type]): The numpy representation of the image.
        bounding_box ([type]): The tuple of the points in the bounding box.

    Returns:
        np.ndarray: The newly copied image with the drawn bounding box.
    """
    new_img = img.copy()
    ((ulx, uly), (urx, _), (_, bly), (_, _)) = bounding_box
    for i in range(uly, bly + 1):
        if i == uly or i == bly:
            new_img[i, ulx : urx + 1] = 1
        else:
            new_img[i, ulx] = 1
            new_img[i, urx] = 1
    return new_img

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
    """ Show the image in a gray scale manner

    Args:
        img (np.ndarray): the numpy representation of the matrix.
    """
    plt.figure()
    plt.imshow(img, cmap=plt.cm.gray)
    plt.show()

def get_mean(img: np.ndarray) -> float:
    return np.mean(img)

def get_variance(img: np.ndarray) -> float:
    return np.var(img)

def get_center_horizontal_baselines(horizontal_baselines: List[int]) -> int:
    horizontal_baselines.sort()
    return horizontal_baselines[len(horizontal_baselines) // 2]

def get_bounding_box(horizontal_baselines: np.ndarray, 
                    vertical_baselines: np.ndarray) -> Tuple[Tuple[int]]:
    hb = np.sort(horizontal_baselines)
    vb = np.sort(vertical_baselines)

    ul = (vb[0], hb[0])
    ur = (vb[-1], hb[0])
    bl = (vb[0], hb[-1])
    br = (vb[-1], hb[-1])
    return ul, ur, bl, br

def get_top_bottom_image(img: np.ndarray, 
                        bounding_box: Tuple[Tuple[int]], 
                        mid: int) -> Tuple[np.ndarray]:
    """ Given an image, a bounding box, and a middle baseline, separate,
    the bounding box section into 2 part, the top part and the bottom part.

    Args:
        img (np.ndarray): the image itself
        bounding_box (Tuple[Tuple[int]]): the bounding box containing 4 points in the
            image. Each point is a tuple of 2 element representing the x-index (column
            index) and y-index (row index).
        mid (int): the index of the middle baseline.

    Returns:
        np.ndarray: The numpy representation of the top bounding box portion.
        np.ndarray: The numpy representation of the bottom bounding box portion.
    """
    ((ulx, uly), (urx, _), (blx, bly), (brx, _)) = bounding_box
    return img[uly:mid, ulx:urx], img[mid:bly, blx:brx]

def get_feature_vector(img: np.ndarray, verbose: bool=False) -> np.ndarray:
    """ Given an image, extract the feature of that image by performing
    discrete wavelet transform, extracting mean and variancce from those
    and from the top and bottom bounding box portion. Each tuple of mean 
    and variance is considered as a feature in a feature vector.

    Args:
        img (np.ndarray): The numpy representation of the image.
        verbose (bool): Whether to print the output or not. Defaults to False.

    Returns:
        np.ndarray: The numpy representation of the feature vector.
            Shape: (17, 2). The 15 first features is the (mean, variance) from
            15 feature images that was wavelet decomposited. The last 2 features
            is the (mean, variance) of the top and bottom portion in the bounding
            box.
    """
    # Define the feature vector matrix
    feature_vector: List[List[float]] = []

    # For the first type of features (15 features of tuple containing mean and var)
    # Do the 2 level dwt.
    # Extract mean and variance of the 2-level of 15 feature imgs, but not the
    # low pass one.
    # Append to the feature vector.
    level_1_transform = list(dwt(img)) # This method will return LL, LH, HL, HH
    level_2_transform = []
    for f in level_1_transform:
        # concatenate new tranformed feature images for each transformed portion
        # in level 1 transform.
        level_2_transform = [*level_2_transform, *list(dwt(f))]

    # For each feature image in the level 2 transform.
    for i, data in enumerate(level_2_transform):
        # If it's not the lowpass of the lowpass in level 2, compute mean and variance,
        # and add it to the feature vector.
        if i > 0:
            feature_vector.append([get_mean(data), get_variance(data)])

    # For the second type of feature (3 feature of tuple containing mean an var)
    # Get the mean and var of the upper-nose part and the bottom-nose part.
    # Append to the feature vector. In this case we use sobel filter to detech edge.

    # Get vertical baselines
    edges = filters.sobel(img)

    # Get the middle horizontal line index of the image.
    mid = edges.shape[0] // 2

    # Compute the left and right verticle lines that bound the face.
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

    if verbose:
        # Print information
        print("Vertical baselines: " + str([left_bound_id, right_bound_id]))
        vertical_baseline_img = draw_vertical_baselines(edges, [left_bound_id, right_bound_id], img)
        show_img(vertical_baseline_img)

    # Compute the index of horizontal baselines
    vp = vertical_projection(edges[3:-3, :]) # Since the projection in the border of the
                                                # image is noisy and trivial, crop those parts
                                                # and perform vertical projection on the cropped
                                                # to get the sum of each horizontal line.
    # Initialize horizontal baselines index vector.
    horizontal_baselines = []

    # What happen next is magic!
    # This kind of data structure is a collection that only contains unique value.
    s = set()

    # Number of maximum index that we want to get
    n = 6

    # the range in the image the the lines have to be far away from each other.
    # I.e, each line index should be more than 20 lines far away from each other.
    range_threshold = 20

    # The sorted indices in the vertical projection (largest to smaller).
    # This sorted indices are also the line horizontal line index in the image.
    sorted_idx = (-vp).argsort()

    # traverse index in the soreted idx vector.
    i = 0 

    # For each line starting from the largest, if we still want to get more index and
    # we have not reached the end of the sorted idx matrix (the number of vertical projection):
    while n > 0 and i < len(sorted_idx):
        # If the line is not in the set, then process
        if sorted_idx[i] not in s:
            # Append it to the horizontal baseline.
            horizontal_baselines.append(sorted_idx[i])
            
            # Also add that to the set so next time we don't add this line to the horizontal 
            # baseline vector.
            s.add(sorted_idx[i])

            # For each line in 20 line up and 20 line down, we also add it to the set
            # So next time we don't add any of those to the horizontal baseline vector.
            for k in range(range_threshold):
                s.add(sorted_idx[i] - k)
                s.add(sorted_idx[i] + k)
            n -= 1
        i += 1

    if verbose:
        # Print information
        print("Horizontal baselines: " + str(horizontal_baselines))
        horizontal_baselines_img = draw_horizontal_baselines(edges, horizontal_baselines, img)
        show_img(horizontal_baselines_img)
        print("Every baseline:")
        total_baseline_img = draw_horizontal_baselines(edges, horizontal_baselines, vertical_baseline_img)
        show_img(total_baseline_img)

    # Get the bounding box
    bounding_box = get_bounding_box(np.asarray(horizontal_baselines), 
                                    [left_bound_id, right_bound_id])

    if verbose:
        # Print the bounding box
        print("Draw bounding box")
        bdb = draw_bounding_box(img, bounding_box)
        show_img(bdb)

    # Get the middle horizontal baseline
    mid_baseline = get_center_horizontal_baselines(horizontal_baselines)

    # Get the seperated top and bottom image.
    top_image, bottom_img = get_top_bottom_image(img, bounding_box, mid_baseline)

    # Finally append the two features (mean and variance of the top and bottom bounding
    # box portions) to the feature vector.
    feature_vector.append([get_mean(top_image), get_variance(top_image)])
    feature_vector.append([get_mean(bottom_img), get_variance(bottom_img)])

    if verbose:
        # Print feature vector
        print("Feature vector:")
        print(np.asarray(feature_vector))
        print()
    return np.asarray(feature_vector)


def get_B_distance(f1: List[float], f2: List[float]) -> float:
    """ The idea of the Bhattacharrya distance is referred from this paper:
    https://www.researchgate.net/publication/222546775_Wavelet_Packet_Analysis_for_Face_Recognition

    Args:
        f1 (List[float]): a particular feature with two value [mean, variance]
        f1 (List[float]): a particular feature with two value [mean, variance]

    Returns:
        float: a scalar (number) representation of the distance between 2 features.
    """
    [mean1, var1] = f1
    [mean2, var2] = f2
    return (1 / 4) * (((mean1 - mean2) ** 2) / (var1 + var2)) + \
        (1 / 2) * np.log(((1 / 2) * (var1 + var2)) / math.sqrt(var1 * var2))

def get_feature_distancce(v1: np.ndarray, v2: np.ndarray) -> float:
    """ The idea of computing the distance between 2 image is computed by the sum of the
    distance among each corresponsing feature of each image's feature vector.
    The idea is referred from this paper:
    https://www.researchgate.net/publication/222546775_Wavelet_Packet_Analysis_for_Face_Recognition

    Args:
        v1 (np.ndarray): an image's feature vector. Shape: (17, 2)
        v2 (np.ndarray): an image's feature vector. Shape: (17, 2)

    Returns:
        (float): The distance between 2 vectors.
    """
    s: float = 0
    for f1, f2 in zip(v1, v2):
        s += get_B_distance(f1, f2)
    return s

def discrete_comparison(v1: np.ndarray, v2: np.ndarray, distance_threshold: float=0.1) -> bool:
    """ Determine if the image is the same person or not.

    Args:
        v1 (np.ndarray): an image's feature vector. Shape: (17, 2)
        v2 (np.ndarray): an image's feature vector. Shape: (17, 2)
        distance_threshold (float, optional): The distance threshold. Defaults to 0.1.

    Returns:
        bool: Return True if the distance of 2 feature vectors is smaller than distance_threshold,
            False otherwise.
    """
    feature_distance = get_feature_distancce(v1, v2)
    return feature_distance <= distance_threshold

# %%
################## EXPERIMENT! ####################

# Here is the eample of the wavelet transform

# Load image
original = pywt.data.camera()
print("Original image:")
show_img(original)
# Wavelet transform of image, and plot approximation and details
titles = ['Approximation', ' Horizontal detail',
          'Vertical detail', 'Diagonal detail']
coeffs2 = pywt.dwt2(original, 'bior1.3')
LL, (LH, HL, HH) = coeffs2
print("Discrete wavelet transform portions:")
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

# Load the 4 images
img1 = np.asarray(Image.open(TEST_FILE_PATH))
img2 = np.asarray(Image.open(TEST_FILE_PATH_2))
img3 = np.asarray(Image.open(TEST_FILE_PATH_3))
img4 = np.asarray(Image.open(TEST_FILE_PATH_4))

# Show 4 test image
show_img(img1)
show_img(img2)
show_img(img3)
show_img(img4)

print("Wavelet packet decomposition of image 1: ")
show_wavelet_decomposition(img1)

# %%

# Read image 
img: np.ndarray = np.asarray(Image.open(TEST_FILE_PATH))
print("original image:")
show_img(img)
LL, LH, HL, HH = dwt(img)
# show_img(HH)
LL1, LH1, HL1, HH1 = dwt(LL)
# show_img(LH1)
print("Image with baseline:")
vp = vertical_projection(HH1)
horizontal_baselines = get_max_value_indices(vp, 4)
new_LL1 = draw_horizontal_baselines(HH1, horizontal_baselines, LL1)
show_img(new_LL1)


# %%

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

hp = horizontal_projection(edges)

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

new_edges = draw_vertical_baselines(edges, [left_bound_id, right_bound_id], img)
show_img(new_edges)

# %%

vp = vertical_projection(edges[3:-3, :])

horizontal_baselines = []
s = set()
n = 6
range_threshold = 20
i = 0 # traverse index in the soreted idx
sorted_idx = (-vp).argsort()
while n > 0 and i < len(sorted_idx):
    if sorted_idx[i] not in s:
        horizontal_baselines.append(sorted_idx[i])
        s.add(sorted_idx[i])
        for k in range(range_threshold):
            s.add(sorted_idx[i] - k)
            s.add(sorted_idx[i] + k)
        n -= 1
    i += 1

new_edges = draw_horizontal_baselines(edges, horizontal_baselines, new_edges)
show_img(new_edges)

# %%

# bounding box can be represented as ((ulx, uly), (urx, ury), (blx, bly), (brx, bry))
bounding_box = get_bounding_box(np.asarray(horizontal_baselines), [left_bound_id, right_bound_id])

print("Image with bounding box")
img_bounding = draw_bounding_box(img, bounding_box)
show_img(img_bounding)

# %%

# We have seen the overview of the image works. Now is the time to do the actual feature
# extraction and comparison.
# Compare between 2 image.
print("Comparing these 2 images:")
fig = plt.figure()
a = fig.add_subplot(1, 2, 1)
a.set_xticks([])
a.set_yticks([])
a.imshow(img, cmap=plt.cm.gray)
a = fig.add_subplot(1, 2, 2)
a.imshow(img2, cmap=plt.cm.gray)
a.set_xticks([])
a.set_yticks([])
plt.show()

img = np.asarray(Image.open(TEST_FILE_PATH_2))
# You can change the verbose to False to not printing the processed images in the method.
feature_vector = get_feature_vector(img, verbose=True)

img2 = np.asarray(Image.open(TEST_FILE_PATH_3))
# You can change the verbose to False to not printing the processed images in the method.
feature_vector_2 = get_feature_vector(img2, verbose=True)

s = get_feature_distancce(feature_vector, feature_vector_2)

print("Computed distance: " + str(s))

same_person = discrete_comparison(feature_vector, feature_vector_2)
if same_person:
    print("The two images are the same person.")
else:
    print("The two images are the different person.")

