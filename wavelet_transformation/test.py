# %%

import numpy as np

from notebook import \
    horizontal_projection,\
    vertical_projection,\
    draw_baselines,\
    get_max_value_indices,\
    dwt

def test_horizontal_projection():
    arr = np.asarray([[1,2],[3,4]])
    print("Original arr " + str(arr))
    print(horizontal_projection(arr))

def test_vertical_projection():
    arr = np.asarray([[1,2],[3,4]])
    print("Original arr " + str(arr))
    print(vertical_projection(arr))

def test_draw_baselines():
    arr = np.asarray([[1,2],[3,4]])
    print("Original arr " + str(arr))
    new_arr = draw_baselines(arr, [1])
    print("Drawn baselines arr " + str(new_arr))


# %%

## Main ##

test_draw_baselines()



