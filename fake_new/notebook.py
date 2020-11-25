# %%

import sklearn
from sklearn.model_selection import train_test_split
import numpy as np
from pathlib import Path
import collections
import pandas as pd

# CONFIGURATIONs

DATA_FOLDER = Path("./data")
FAKE_DATA_NAME = "clean_fake.txt"
FAKE_DATA_PATH = DATA_FOLDER / FAKE_DATA_NAME
REAL_DATA_NAME = "clean_real.txt"
REAL_DATA_PATH = DATA_FOLDER / REAL_DATA_NAME

# %%

# Fake = 0
# Real = 1

# Read fake data
X = []
Y = []
fake_x = []
with open (FAKE_DATA_PATH, "r") as f:
    for line in f:
        X.append(line[:-2])
        Y.append(0)
        fake_x.append(line[:-2])

real_x = []
with open (REAL_DATA_PATH, "r") as f:
    for line in f:
        X.append(line[:-2])
        Y.append(1)
        real_x.append(line[:-2])

# X = np.asarray(X)
# X = X.reshape((-1,1))
# Y = np.asarray(Y)
# Y = Y.reshape((-1,1))

# %%

# # SPlit train and other parts!
# x_train, y_train, x_, y_ = train_test_split(X, Y, test_size=0.3)
# # Split test and validation for those parts!
# x_test, y_test, x_val, y_val = train_test_split(x_, y_, test_size=0.5)
# %%

x_train = X[:-100]
y_train = Y[:-100]
x_test = X[-100:]
y_test = Y[-100:]

# %%

from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.naive_bayes import MultinomialNB
from sklearn.pipeline import make_pipeline

model = make_pipeline(TfidfVectorizer(), MultinomialNB())

model.fit(x_train, y_train)
labels = model.predict(x_test)

# %%
# from sklearn.metrics import confusion_matrix
# mat = confusion_matrix(test.target, labels)
# sns.heatmap(mat.T, square=True, annot=True, fmt='d', cbar=False,
#             xticklabels=train.target_names, yticklabels=train.target_names)
# plt.xlabel('true label')
# plt.ylabel('predicted label')


# def predict_category(s, train=train, model=model):
#     pred = model.predict([s])
#     return train.target_names[pred[0]]

# %%

# Get the most presence word

cnt_real = collections.Counter()
for line in real_x:
    words = line.split()
    for w in words:
        cnt_real[w] += 1

cnt_fake = collections.Counter()
for line in fake_x:
    words = line.split()
    for w in words:
        cnt_fake[w] += 1


# %%

# Write csv
df = pd.DataFrame(cnt_fake.items())
df.to_csv("./analysis/fake_words.csv")

df = pd.DataFrame(cnt_real.items())
df.to_csv("./analysis/real_words.csv")


# %%

from sklearn.feature_extraction.text import ENGLISH_STOP_WORDS

# %%

# Get the most presence word

cnt_real_non_stop = collections.Counter()
for line in real_x:
    words = line.split()
    for w in words:
        if w not in ENGLISH_STOP_WORDS:
            cnt_real_non_stop[w] += 1

cnt_fake_non_stop = collections.Counter()
for line in fake_x:
    words = line.split()
    for w in words:
        if w not in ENGLISH_STOP_WORDS:
            cnt_fake_non_stop[w] += 1

# Write csv
df = pd.DataFrame(cnt_fake_non_stop.items())
df.to_csv("./analysis/fake_words_non_stop.csv")

df = pd.DataFrame(cnt_real_non_stop.items())
df.to_csv("./analysis/real_words_non_stop.csv")

# %%

# One-hot encode the data

word_to_id = {}
id_to_word = {}
c = 0
for line in X:
    words = line.split()
    for w in words:
        if w not in word_to_id:
            word_to_id[w] = c
            id_to_word[c] = w
            c += 1

# %%

# len(word_to_id.items())
def encode(line: str) -> np.array:
    ans = [0] * len(word_to_id)
    for w in line.split():
        ans[word_to_id[w]] = 1
    return np.asarray(ans)

# %%
test_case = 3
test_arr = encode(X[test_case])
test_arr

# line_bit = int("".join([str(d) for d in test_arr]))
# # count set bit
# cnt_bit = 0
# while line_bit:
#     line_bit &= (line_bit - 1)
#     cnt_bit += 1

# cnt_bit

cnt = 0
for n in test_arr:
    if n == 1:
        cnt += 1
print(cnt)

len(X[test_case].split())


# %%%
import random
x_train = []
y_train = []
for line in X:
    encoded = encode(line)
    x_train.append(encoded)
    y_train.append(random.randint(0,1))

# %%

# import the class
from sklearn.linear_model import LogisticRegression

# instantiate the model (using the default parameters)
logreg = LogisticRegression()

# fit the model with data
logreg.fit(x_train,y_train)

#
# y_pred=logreg.predict(X_test)

logreg.predict(x_train[:3])




