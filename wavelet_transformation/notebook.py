# %%
import matplotlib.pyplot as plt

from typing import List, Dict, Tuple
import numpy as np
import pywt
import pywt.data

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

def draw_baselines(img: np.ndarray, 
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
        drawn[baseline,:] = 255
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
new_LL1 = draw_baselines(HH1, ids, LL1)
show_img(new_LL1)

