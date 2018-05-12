import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from textwrap import wrap


metadata = pd.read_csv('./the-movies-dataset/movies_metadata.csv', low_memory=False)

# Calculation based on the IMDB formula
# Weighted Rating (WR) = (v/(v+m) * R) + (m/(m+v) * C)
# where,

# v is the number of votes for the movie;
# m is the minimum votes required to be listed in the chart;
# R is the average rating of the movie; And
# C is the mean vote across the whole report
C = metadata['vote_average'].mean()

m = metadata['vote_count'].quantile(0.90)

valid_movies = metadata.copy().loc[metadata['vote_count'] >= m]

def weighted_rating(x, m=m, C=C):
    v = x['vote_count']
    R = x['vote_average']
    return (v/(v+m) * R) + (m/(m+v) * C)

valid_movies['score'] = valid_movies.apply(weighted_rating, axis=1)
valid_movies = valid_movies.sort_values('score', ascending=False)

print(valid_movies[['title', 'vote_count', 'vote_average', 'score']].head(15))

# titles = np.array(valid_movies[['title']].head(15)).reshape(15,1)
# score = np.array(valid_movies[['score']].head(15))

# from pylab import rcParams
# rcParams['figure.figsize'] = 35,20

# titles = [ '\n'.join(wrap(l, 10)) for l in titles ]
# # print(titles.shape)
# # print(score.shape)

# # ind = np.arange(1,len(score)+1)

# ind = np.arange(1,len(score)+1)
# print(titles)
# print(ind)
# plt.title("")
# plt.ylabel("Score")
# plt.xlabel("Movie Title")
# plt.bar(ind, score, align = 'center')
# plt.legend()
# plt.show()