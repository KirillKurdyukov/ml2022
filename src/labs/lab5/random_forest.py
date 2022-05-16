import math
import numpy as np

from decision_tree import DecisionTreeClassifier


# https://github.com/eriklindernoren/ML-From-Scratch/blob/master/mlfromscratch/supervised_learning/random_forest.py
class RandomForestClassifier:

    def __init__(self, n_estimators: int = 10):
        self.n_estimators = n_estimators
        self.trees = []
        self.trees_feature_indices = []
        self.max_features = None
        for _ in range(self.n_estimators):
            self.trees.append(DecisionTreeClassifier())
            self.trees_feature_indices.append(None)
        return

    def fit(self, X, y):
        n_features = np.shape(X)[1]
        self.max_features = int(math.sqrt(n_features))
        subsets = get_random_subsets(X, y, self.n_estimators)
        for i in range(self.n_estimators):
            X_subset, y_subset = subsets[i]
            idx = np.random.choice(range(n_features), size=self.max_features, replace=True)
            self.trees_feature_indices[i] = idx
            X_subset = X_subset[:, idx]
            self.trees[i].fit(X_subset, y_subset)

    def predict(self, X):
        y_preds = np.empty((X.shape[0], len(self.trees)))
        for i, tree in enumerate(self.trees):
            idx = self.trees_feature_indices[i]

            prediction = tree.predict(X[:, idx])
            y_preds[:, i] = prediction

        y_pred = []
        for sample_predictions in y_preds:
            y_pred.append(np.bincount(sample_predictions.astype('int')).argmax())

        return y_pred


def get_random_subsets(X, y, n_subsets, replacements=True):
    n_samples = np.shape(X)[0]
    X_y = np.concatenate((X, y.reshape((1, len(y))).T), axis=1)
    np.random.shuffle(X_y)
    subsets = []

    subsample_size = int(n_samples // 2)
    if replacements:
        subsample_size = n_samples

    for _ in range(n_subsets):
        idx = np.random.choice(
            range(n_samples),
            size=np.shape(range(subsample_size)),
            replace=replacements)
        X = X_y[idx][:, :-1]
        y = X_y[idx][:, -1]
        subsets.append([X, y])
    return subsets
