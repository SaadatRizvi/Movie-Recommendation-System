import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import linear_kernel
import matplotlib.pyplot as plt; plt.rcdefaults()
import numpy as np
import matplotlib.pyplot as plt
from textwrap import wrap


metadata = pd.read_csv('./the-movies-dataset/movies_metadata-verysmall.csv', low_memory=False)

# Remove all english stop words such as 'the', 'a'
tfidf = TfidfVectorizer(stop_words='english')

metadata['overview'] = metadata['overview'].fillna('')

tfidf_matrix = tfidf.fit_transform(metadata['overview'])
#print(tfidf_matrix.shape)

# Compute the cosine similarity matrix
cosine_sim = linear_kernel(tfidf_matrix, tfidf_matrix)

#Construct a reverse map of indices and movie titles
indices = pd.Series(metadata.index, index=metadata['title']).drop_duplicates()
#print(indices)
def get_recommendations(title, cosine_sim=cosine_sim):
    # Get the index of the movie that matches the title
    index = indices[title]

    # Get the pairwsie similarity scores of all movies with that movie
    sim_scores = list(enumerate(cosine_sim[index]))

    # Sort the movies based on the similarity scores
    sim_scores = sorted(sim_scores, key=lambda x: x[1], reverse=True)

    # Get the scores of the 10 most similar movies
    sim_scores = sim_scores[1:11]

    # Get the movie indices
    movie_indices = [i[0] for i in sim_scores]
    
    topMovies = metadata['title'].iloc[movie_indices]

    movie_ratings = [i[1] for i in sim_scores]

    topMovies['Score'] = movie_ratings

    # Return the top 10 most similar movies
    return  topMovies


#topMovies = get_recommendations('Persuasion')
#topMovies = get_recommendations('The Godfather')


topMovies = get_recommendations('Toy Story')
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
