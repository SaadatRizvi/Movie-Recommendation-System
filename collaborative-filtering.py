import pandas as panda

r_cols = ['user_id', 'movie_id', 'rating']
ratings = panda.read_csv('u.data', sep='\t', names=r_cols, usecols=range(3))

m_cols = ['movie_id', 'title']
movies = panda.read_csv('u.item', sep='|', names=m_cols, usecols=range(2))


ratings = panda.merge(movies, ratings)      #all ratings of all movies

userRatings = ratings.pivot_table(index=['user_id'],columns=['title'],values='rating')  #convert table data into matrix form
#userRatings.to_csv("userRatings.csv") 
corrMatrix = userRatings.corr(method='pearson', min_periods=100)    #apply pearson co-relation formula with minimum number of observations required per pair of column to be 100
#corrMatrix.to_csv("corrMatrix.csv")
userId = 2
#print("Movies and their ratings for user ", userId)
myRatings = userRatings.loc[userId].dropna()    #loc = indexed location. dropna() = drop all NaN values. Filters out all movies for which user with userId didn't give any rating
#myRatings.to_csv("myRatings.csv")
simCandidates = panda.Series()
for i in range(0, len(myRatings.index)):
    sims = corrMatrix[myRatings.index[i]].dropna()
    sims = sims.map(lambda x: x * myRatings[i])
    simCandidates = simCandidates.append(sims)
#simCandidates.to_csv("simCandidates.csv")
simCandidates.sort_values(inplace = True, ascending = False)

simCandidates = simCandidates.groupby(simCandidates.index).sum()

simCandidates.sort_values(inplace = True, ascending = False)
filteredSims = simCandidates.drop(myRatings.index, errors='ignore')
print(filteredSims.head())
filteredSims.to_csv("filteredSims.csv")