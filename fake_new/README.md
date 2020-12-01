# Analysis of fakenews project - CS 371
Author: Alex Nguyen and Hao Lin | Gettysburg College
------------------

## Structure of the repository

* The [`README.md`](./README.md) is the main written answer file that reader should follow in addition to the code written in [`notebook.py`](./notebook.py) and [`notebook.ipynb`](./notebook.ipynb).

* The [analysis folder](./analysis) contains csv files that analyze the nature of the data.

* The [data folder](./data) contains the data files.

* The file [`notebook.py`](./notebook.py) is the main file that contains the main code analysis

* The file [`notebook.ipynb`](./notebook.ipynb) is the main notebook that contains the main code analysis

* [Here](https://colab.research.google.com/drive/1CniVqlrgH_wxul13CTXURzmqkxM3zVH8?usp=sharing) is the editable link to the google colab jupyter notebook. <b>Note:</b> Please upload the required folders and data files in order for the notebook to work.
------------------

## Part 1: 
- Describe the datasets. You will be predicting whether a headline is real or fake news from words that appear in the headline. Is that feasible? Give 3 examples of specific keywords that may be useful, together with statistics on how often they appear in real and fake headlines.
- For the rest of the project, you should split your dataset into ~70% training, ~15% validation, and ~15% test.

<b>Answer:</b>
* According to [`fake_word_non_stop.csv`](./analysis/fake_word_non_stop.csv) (all distinct words in the fake new and its occurrence that does not include the stop words), we can see that the most frequent keys is "Trump", "Donald", and "Hilary".
* The data was splited in the train, test, and validationin set in [`notebook.py`](./notebook.py).

## Part 2:

* Implemented in the [`notebook.py`](./notebook.py) and [`notebook.ipynb`](./notebook.ipynb).

## Part 3:
### Part 3a:
* According to the [`real_words.csv`](./analysis/real_words.csv), sorting the csv file in ascending and descending order in the number of presence gives us the least and most frequent keywords, respectively:
  * The most frequent keywords in real news: 'donald', 'to', 'us', 'trumps', 'in', 'on', 'of', 'for', 'the'.
  * The least frequent keywords in real news: 'ba', 'how', 'climate', 'obama', 'house', 'has', 'first', 'he', 'not', 'what'.

* According to the [`fake_words.csv`](./analysis/fake_words.csv), sorting the csv file in ascending and descending order in the number of presence gives us the least and most frequent keywords, respectively:
  * The most frequent keywords in fake news: 'trump', 'the', 'to', 'in', 'donald', 'of', 'for', 'a', 'and', 'on'.

  * The least frequent keywords in fake news: 'why', 'after', 'campaign', 'america', 'voter', 'vote', 'not', 'supporter', 'about', 'says'.

### Part 3b:
* According to the [`real_words_non_stop.csv`](./analysis/real_words_non_stop.csv), sorting the csv file in descending order in the number of presence gives us the most frequent keywords:
  * The most frequent keywords in real news: 'donald', 'trumps', 'says', 'trum', 'north', 'election', 'clinton', 'president', 'russia', 'korea'.

* According to the [`fake_words_non_stop.csv`](./analysis/fake_words_non_stop.csv), sorting the csv file in descending order in the number of presence gives us the most frequent keywords:
  * The most frequent keywords in fake news: 'trump', 'donald', 'hillary', 'clinton', 'trum', 'new', 'just', 'election', 'obama', 'president'


### Part 3c:
* It is important to remove stop words from the model because ...
* It is important to keep stop words from the model because ...

## Part 4:
* Implemented in the [`notebook.py`](./notebook.py) and [`notebook.ipynb`](./notebook.ipynb).

## Part 5:
* Implemented in the [`notebook.py`](./notebook.py) and [`notebook.ipynb`](./notebook.ipynb).

## Part 6:
* Implemented in the [`notebook.py`](./notebook.py) and [`notebook.ipynb`](./notebook.ipynb).

* Most frequent vs max coefficient:

 max coefficient: [1.6687395653110482,
 1.6645178631109863,
 1.6481661544906379,
 1.401782153490334,
 1.3387271561830725,
 1.236125668594303,
 1.1732490921287593,
 1.1652068741382289,
 1.1632194318665803,
 1.0941847441464467]

vs.

Most frequent probability: [0.10635443346749604, 0.050554945725088424, 0.02518599829247469, 0.01402610074399317, 0.013355287230150018, 0.01305037199658495, 0.012501524576167825, 0.011342846688620564, 0.010854982314916453, 0.010611050128064398]


* Least frequent vs min coefficient:

min coefficient: [-2.075439801899081,
 -1.9044240885094594,
 -1.5444787571822136,
 -1.5365008779591092,
 -1.4908437184771457,
 -1.4446385380688067,
 -1.4325816440323556,
 -1.4281720023015625,
 -1.2832294377474074,
 -1.191112797598666]

vs.

Least frequent probability: [0.10635443346749604, 0.050554945725088424, 0.02518599829247469, 0.01402610074399317, 0.013355287230150018, 0.01305037199658495, 0.012501524576167825, 0.011342846688620564, 0.010854982314916453, 0.010611050128064398]

## Part 7: 

* Implemented in the [`notebook.py`](./notebook.py) and [`notebook.ipynb`](./notebook.ipynb).