# %%

from typing import List, Dict, Tuple

import random
import numpy as np
from pathlib import Path
import collections
import pandas as pd

import sklearn
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.linear_model import LogisticRegression
from sklearn.naive_bayes import MultinomialNB
from sklearn.tree import DecisionTreeClassifier
from sklearn.model_selection import train_test_split


from sklearn.feature_extraction.text import ENGLISH_STOP_WORDS

# CONFIGURATIONs

DATA_FOLDER = Path("./data")
FAKE_DATA_NAME = "clean_fake.txt"
FAKE_DATA_PATH = DATA_FOLDER / FAKE_DATA_NAME
REAL_DATA_NAME = "clean_real.txt"
REAL_DATA_PATH = DATA_FOLDER / REAL_DATA_NAME

def read_data(real_path, fake_path, stop_words : List[str]=[]):
    """
        Given a path to real data and fake data, return 2 2d-array of word 
        with shape(n_sentences, n_words_in_sentence)
    """
    fake: List[str] = []
    with open (fake_path, "r") as f:
        for line in f:
            tmp = []
            for w in line.strip().split():
                if w not in stop_words:
                    tmp.append(w)
            fake.append(tmp)
    real: List[str] = []
    with open (real_path, "r") as f:
        for line in f:
            tmp = []
            for w in line.strip().split():
                if w not in stop_words:
                    tmp.append(w)
            real.append(tmp)
    return np.asarray(real), np.asarray(fake)

# Get the most presence word
def export_most_presence_words(real_x: List[str], fake_x: List[str]):
    total_real = 0
    cnt_real = collections.Counter()
    for line in real_x:
        words = line.split()
        for w in words:
            cnt_real[w] += 1
            total_real += 1

    cnt_fake = collections.Counter()
    for line in fake_x:
        words = line.split()
        for w in words:
            cnt_fake[w] += 1

    most_presence = np.asarray(cnt_real.most_common(10))
    # print(most_presence.shape)
    most_presence_probs = [int(data) / total_real for data in most_presence[:,1]]
    print("Most presence words probability:\n" + str(most_presence_probs))
    least_presence = np.asarray(cnt_real.most_common()[:-10-1:-1])
    least_presence_probs = [int(data) / total_real for data in least_presence[:,1]]
    print("Least presence words probability:\n" + str(least_presence_probs))

    # Write csv
    df = pd.DataFrame(cnt_fake.items())
    df.to_csv("./analysis/fake_words.csv")

    df = pd.DataFrame(cnt_real.items())
    df.to_csv("./analysis/real_words.csv")

# Get the most presence word
def export_most_presence_words_with_stop_words(real_x: List[str], fake_x: List[str]):
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

real_x, fake_x = read_data(REAL_DATA_PATH, FAKE_DATA_PATH)

real_lines = np.asarray([" ".join(row) for row in real_x])
fake_lines = np.asarray([" ".join(row) for row in fake_x])
real_y = np.asarray(len(real_lines) * [1])
fake_y = np.asarray(len(fake_lines) * [0])

data_lines = np.append(real_lines, fake_lines, axis=0)
data_label = np.append(real_y, fake_y, axis=0)

random.seed(0)
random.shuffle(data_lines)
random.seed(0)
random.shuffle(data_label)

# # SPlit train and other parts!
x_train, x_, y_train, y_ = train_test_split(data_lines, data_label, test_size=0.3)
# # Split test and validation for those parts!
x_test, x_val, y_test, y_val = train_test_split(x_, y_, test_size=0.5)

# %%

# Vectorize train test
vectorizer = CountVectorizer()
counts_train = vectorizer.fit_transform(x_train)
counts_test = vectorizer.transform(x_test)

classifier = MultinomialNB()
classifier.fit(counts_train, y_train)
predictions = classifier.predict(counts_test)
print('Testing accuracy for Naive Bayes =', sum(predictions == y_test) / len(y_test))

regression = LogisticRegression()
regression.fit(counts_train, y_train)
predictions = regression.predict(counts_test)
print('Testing accuracy Logistic Regression =', sum(predictions == y_test) / len(y_test))


# %%

coef = np.asarray(regression.coef_)
# np.max(coef)

coef = coef.flatten()

# %%


max_args = (-coef).argsort()[:10]
max_probs = [coef[arg] for arg in max_args]
min_args = (coef).argsort()[:10]
min_probs = [coef[arg] for arg in min_args]

print(max_probs)
print(min_probs)

# %%

export_most_presence_words(real_lines, fake_lines)

# %%


# Decision tree classifier with normal data

dtree = DecisionTreeClassifier()
dtree.fit(counts_train, y_train)
predictions = dtree.predict(counts_test)
print('Testing accuracy Decision Tree classifier =', sum(predictions == y_test) / len(y_test))


# %%

# Redo the whole process with non-stop-word

real_x, fake_x = read_data(REAL_DATA_PATH, FAKE_DATA_PATH, ENGLISH_STOP_WORDS)

real_lines = np.asarray([" ".join(row) for row in real_x])
fake_lines = np.asarray([" ".join(row) for row in fake_x])
real_y = np.asarray(len(real_lines) * [1])
fake_y = np.asarray(len(fake_lines) * [0])

data_lines = np.append(real_lines, fake_lines, axis=0)
data_label = np.append(real_y, fake_y, axis=0)

random.seed(0)
random.shuffle(data_lines)
random.seed(0)
random.shuffle(data_label)

# # SPlit train and other parts!
x_train, x_, y_train, y_ = train_test_split(data_lines, data_label, test_size=0.3)
# # Split test and validation for those parts!
x_test, x_val, y_test, y_val = train_test_split(x_, y_, test_size=0.5)

# Vectorize train test
vectorizer = CountVectorizer()
counts_train = vectorizer.fit_transform(x_train)
counts_test = vectorizer.transform(x_test)

classifier = MultinomialNB()
classifier.fit(counts_train, y_train)
predictions = classifier.predict(counts_test)
print('Testing accuracy for Naive Bayes =', sum(predictions == y_test) / len(y_test))

regression = LogisticRegression()
regression.fit(counts_train, y_train)
predictions = regression.predict(counts_test)
print('Testing accuracy Logistic Regression =', sum(predictions == y_test) / len(y_test))

coef = np.asarray(regression.coef_)

coef = coef.flatten()

max_args = (-coef).argsort()[:10]
max_probs = [coef[arg] for arg in max_args]
min_args = (coef).argsort()[:10]
min_probs = [coef[arg] for arg in min_args]

print(max_probs)
print(min_probs)


# Decision tree classifier for nonstop word

dtree = DecisionTreeClassifier()
dtree.fit(counts_train, y_train)
predictions = dtree.predict(counts_test)
print('Testing accuracy Decision Tree classifier =', sum(predictions == y_test) / len(y_test))


# %%
print("Trying different depth from 1 to 100...")
for i in range(10):
    max_depth = np.random.randint(1,100)
    dtree = DecisionTreeClassifier(max_depth=max_depth)
    dtree.fit(counts_train, y_train)
    predictions = dtree.predict(counts_test)
    t = sum(predictions == y_test) / len(y_test)
    print(f'Testing accuracy Decision Tree classifier for depth {max_depth} = {t}')

