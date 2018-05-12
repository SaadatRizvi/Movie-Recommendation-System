#3 top actors,
# the director,
# related genres
#  and the movie plot keywords.
import pandas as pd
import numpy as np
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.metrics.pairwise import cosine_similarity
import matplotlib.pyplot as plt; plt.rcdefaults()
import matplotlib.pyplot as plt
from textwrap import wrap


from ast import literal_eval

metadata = pd.read_csv('./the-movies-dataset/movies_metadata-verysmall.csv', low_memory=False)
credits = pd.read_csv('./the-movies-dataset/credits.csv')
keywords = pd.read_csv('./the-movies-dataset/keywords.csv')

metadata['id'] = metadata['id'].fillna(-1)

keywords['id'] = keywords['id'].astype('int')
credits['id'] = credits['id'].astype('int')
metadata['id'] = metadata['id'].astype('int')

# Add keywords and credits data by perofrming inner join
metadata = metadata.merge(credits, on='id')
metadata = metadata.merge(keywords, on='id')

# #print(metadata.head(2))
# print(metadata.head(2)['cast'])
# print("----------------------------")
# print("----------------------------")
# print("----------------------------")


features1 = ['cast', 'crew', 'keywords', 'genres']
for feature in features1:
    metadata[feature] = metadata[feature].apply(literal_eval)

#print(metadata.head(2)['cast'])

def get_movieDirectorName(x):
    for i in x:
        if i['job'] == 'Director':
            return i['name']
    return np.nan

def get_firstThree(x):
    if isinstance(x, list):
        names = [i['name'] for i in x]
        if len(names) > 3:
            names = names[:3]
        return names

    return []
metadata['director'] = metadata['crew'].apply(get_movieDirectorName)

features2 = ['cast', 'keywords', 'genres']
for feature in features2:
    metadata[feature] = metadata[feature].apply(get_firstThree)

# print(metadata[['title', 'cast', 'director', 'keywords', 'genres']].head(3))

def remove_spaces(x):
    if isinstance(x, list):
        return [str.lower(i.replace(" ", "")) for i in x]
    else:
        if isinstance(x, str):
            return str.lower(x.replace(" ", ""))
        else:
            return ''

features3 = ['cast', 'keywords', 'director', 'genres']

for feature in features3:
    metadata[feature] = metadata[feature].apply(remove_spaces)


def jumble(x):
    return ' '.join(x['keywords']) + ' ' + ' '.join(x['cast']) + ' ' + x['director'] + ' ' + ' '.join(x['genres'])

metadata['jumble'] = metadata.apply(jumble, axis=1)


count = CountVectorizer(stop_words='english')
count_matrix = count.fit_transform(metadata['jumble'])

cosine_sim = cosine_similarity(count_matrix, count_matrix)
metadata = metadata.reset_index()


indices = pd.Series(metadata.index, index=metadata['title'])
def get_recommendations(title, cosine_sim=cosine_sim):
    idx = indices[title]

    sim_scores = list(enumerate(cosine_sim[idx]))

    sim_scores = sorted(sim_scores, key=lambda x: x[1], reverse=True)

    sim_scores = sim_scores[1:11]

    movie_indices = [i[0] for i in sim_scores]
    
    topMovies = metadata['title'].iloc[movie_indices]

    movie_ratings = [i[1] for i in sim_scores]

    topMovies['Score'] = movie_ratings

    # Return the top 10 most similar movies
    return  topMovies

#topMovies = get_recommendations('Persuasion')
topMovies = get_recommendations('The Godfather')


#topMovies = get_recommendations('Toy Story')
print(topMovies)

print("--------------Scores---------------------------")
print(topMovies['Score'])

# Code for bar chart

titles = np.array(topMovies)[0:10]
score = np.array(topMovies['Score'])
# print(titles.shape)
# print(score.shape)

titles = [ '\n'.join(wrap(l, 10)) for l in titles ]

from pylab import rcParams
rcParams['figure.figsize'] = 40,20

ind = np.arange(1,len(score)+1)
# print(ind)
plt.title("")
plt.ylabel("Score")
plt.xlabel("Movie Title")
plt.bar(ind, score,tick_label=titles, align = 'center')
plt.legend()
plt.show()
