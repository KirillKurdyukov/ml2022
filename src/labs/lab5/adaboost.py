import numpy as np

from sklearn.tree import DecisionTreeClassifier


class AdaBoostClassifier:

    def __init__(self):
        self.alphas = []
        self.trees = []
        self.M = None
        self.prediction_errors = []

    @staticmethod
    def compute_error(y, y_pred, w):
        N = 0
        for i in range(len(y)):
            if y_pred[i] != y[i]:
                N += w[i]
        return N

    @staticmethod
    def compute_alpha(error):
        if error == 0:
            return 0
        elif error == 1:
            return 1
        else:
            return 0.5 * np.log((1 - error) / error)

    @staticmethod
    def update_weights(w, alpha, y, y_pred):
        for i in range(len(y)):
            if y[i] != y_pred[i]:
                w[i] = w[i] * np.exp(alpha)
            else:
                w[i] = w[i] * np.exp(-alpha)
        w_sum = np.sum(w)
        for i in range(len(y)):
            w[i] = w[i] / w_sum
        return w

    def fit(self, X, y, M=55):
        self.alphas = []
        self.M = M

        w = []
        for i in range(len(y)):
            w.append(1 / len(y))

        for m in range(0, M):
            tree = DecisionTreeClassifier(max_depth=2)
            tree.fit(X, y, sample_weight=w)
            y_pred = tree.predict(X)
            self.trees.append(tree)
            error = self.compute_error(y, y_pred, w)
            alpha = self.compute_alpha(error)
            self.alphas.append(alpha)
            w = self.update_weights(w, alpha, y, y_pred)

    def predict(self, X):
        result = []

        for x in X:
            sum = 0
            for i in range(self.M):
                pred = self.trees[i].predict([x])[0]
                pred = -1 if pred == 0 else 1
                sum += pred * self.alphas[i]
            result.append(1 if sum > 0 else 0)

        return np.array(result)