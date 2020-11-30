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
from sklearn.model_selection import train_test_split


from sklearn.feature_extraction.text import ENGLISH_STOP_WORDS

# CONFIGURATIONs

DATA_FOLDER = Path("./data")
FAKE_DATA_NAME = "clean_fake.txt"
FAKE_DATA_PATH = DATA_FOLDER / FAKE_DATA_NAME
REAL_DATA_NAME = "clean_real.txt"
REAL_DATA_PATH = DATA_FOLDER / REAL_DATA_NAME

def read_data(real_path, fake_path):
    """

    """
    fake: List[str] = []
    with open (fake_path, "r") as f:
        for line in f:
            fake.append(line.strip().split())
    real: List[str] = []
    with open (real_path, "r") as f:
        for line in f:
            real.append(line.strip().split())
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

# Test 
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




# %%

# from sklearn.feature_extraction.text import TfidfVectorizer
# from sklearn.naive_bayes import MultinomialNB
# from sklearn.pipeline import make_pipeline

# model = make_pipeline(TfidfVectorizer(), MultinomialNB())

# model.fit(x_train, y_train)
# labels = model.predict(x_test)

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





import matplotlib.pyplot as plt
from sklearn.naive_bayes import GaussianNB
from sklearn.svm import SVC
from sklearn.datasets import load_digits
from sklearn.model_selection import learning_curve
from sklearn.model_selection import ShuffleSplit


def plot_learning_curve(estimator, title, X, y, axes=None, ylim=None, cv=None,
                        n_jobs=None, train_sizes=np.linspace(.1, 1.0, 5)):
    """
    Generate 3 plots: the test and training learning curve, the training
    samples vs fit times curve, the fit times vs score curve.

    Parameters
    ----------
    estimator : object type that implements the "fit" and "predict" methods
        An object of that type which is cloned for each validation.

    title : string
        Title for the chart.

    X : array-like, shape (n_samples, n_features)
        Training vector, where n_samples is the number of samples and
        n_features is the number of features.

    y : array-like, shape (n_samples) or (n_samples, n_features), optional
        Target relative to X for classification or regression;
        None for unsupervised learning.

    axes : array of 3 axes, optional (default=None)
        Axes to use for plotting the curves.

    ylim : tuple, shape (ymin, ymax), optional
        Defines minimum and maximum yvalues plotted.

    cv : int, cross-validation generator or an iterable, optional
        Determines the cross-validation splitting strategy.
        Possible inputs for cv are:

          - None, to use the default 5-fold cross-validation,
          - integer, to specify the number of folds.
          - :term:`CV splitter`,
          - An iterable yielding (train, test) splits as arrays of indices.

        For integer/None inputs, if ``y`` is binary or multiclass,
        :class:`StratifiedKFold` used. If the estimator is not a classifier
        or if ``y`` is neither binary nor multiclass, :class:`KFold` is used.

        Refer :ref:`User Guide <cross_validation>` for the various
        cross-validators that can be used here.

    n_jobs : int or None, optional (default=None)
        Number of jobs to run in parallel.
        ``None`` means 1 unless in a :obj:`joblib.parallel_backend` context.
        ``-1`` means using all processors. See :term:`Glossary <n_jobs>`
        for more details.

    train_sizes : array-like, shape (n_ticks,), dtype float or int
        Relative or absolute numbers of training examples that will be used to
        generate the learning curve. If the dtype is float, it is regarded as a
        fraction of the maximum size of the training set (that is determined
        by the selected validation method), i.e. it has to be within (0, 1].
        Otherwise it is interpreted as absolute sizes of the training sets.
        Note that for classification the number of samples usually have to
        be big enough to contain at least one sample from each class.
        (default: np.linspace(0.1, 1.0, 5))
    """
    if axes is None:
        _, axes = plt.subplots(1, 3, figsize=(20, 5))

    axes[0].set_title(title)
    if ylim is not None:
        axes[0].set_ylim(*ylim)
    axes[0].set_xlabel("Training examples")
    axes[0].set_ylabel("Score")

    train_sizes, train_scores, test_scores, fit_times, _ = \
        learning_curve(estimator, X, y, cv=cv, n_jobs=n_jobs,
                       train_sizes=train_sizes,
                       return_times=True)
    train_scores_mean = np.mean(train_scores, axis=1)
    train_scores_std = np.std(train_scores, axis=1)
    test_scores_mean = np.mean(test_scores, axis=1)
    test_scores_std = np.std(test_scores, axis=1)
    fit_times_mean = np.mean(fit_times, axis=1)
    fit_times_std = np.std(fit_times, axis=1)

    # Plot learning curve
    axes[0].grid()
    axes[0].fill_between(train_sizes, train_scores_mean - train_scores_std,
                         train_scores_mean + train_scores_std, alpha=0.1,
                         color="r")
    axes[0].fill_between(train_sizes, test_scores_mean - test_scores_std,
                         test_scores_mean + test_scores_std, alpha=0.1,
                         color="g")
    axes[0].plot(train_sizes, train_scores_mean, 'o-', color="r",
                 label="Training score")
    axes[0].plot(train_sizes, test_scores_mean, 'o-', color="g",
                 label="Cross-validation score")
    axes[0].legend(loc="best")

    # Plot n_samples vs fit_times
    axes[1].grid()
    axes[1].plot(train_sizes, fit_times_mean, 'o-')
    axes[1].fill_between(train_sizes, fit_times_mean - fit_times_std,
                         fit_times_mean + fit_times_std, alpha=0.1)
    axes[1].set_xlabel("Training examples")
    axes[1].set_ylabel("fit_times")
    axes[1].set_title("Scalability of the model")

    # Plot fit_time vs score
    axes[2].grid()
    axes[2].plot(fit_times_mean, test_scores_mean, 'o-')
    axes[2].fill_between(fit_times_mean, test_scores_mean - test_scores_std,
                         test_scores_mean + test_scores_std, alpha=0.1)
    axes[2].set_xlabel("fit_times")
    axes[2].set_ylabel("Score")
    axes[2].set_title("Performance of the model")

    return plt


fig, axes = plt.subplots(3, 2, figsize=(10, 15))

title = "Learning Curves"
cv = ShuffleSplit(n_splits=10, test_size=0.2, random_state=0)
plot_learning_curve(regression, title, x_train, y_train, axes=axes[:, 1], ylim=(0.7, 1.01),
                    cv=cv, n_jobs=4)

plt.show()


# %%

