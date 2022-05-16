import numpy as np


# https://towardsdatascience.com/implementing-a-decision-tree-from-scratch-f5358ff9c4bb
class Node:
    def __init__(self, feature=None, threshold=None, left=None, right=None, *, value=None):
        self.feature = feature
        self.threshold = threshold
        self.left = left
        self.right = right
        self.value = value
        self.sample_weight = None

    def is_leaf(self):
        return self.value is not None


class DecisionTreeClassifier:
    def __init__(self, max_depth=100, min_samples_split=2, sample_weight=None):
        self.max_depth = max_depth
        self.min_samples_split = min_samples_split
        self.sample_weight = sample_weight
        self.root = None

    def _is_finished(self, depth):
        if (depth >= self.max_depth
                or self.n_class_labels == 1
                or self.n_samples < self.min_samples_split):
            return True
        return False

    @staticmethod
    def _entropy(y):
        proportions = np.bincount(y.astype(int)) / len(y)
        entropy = -np.sum([p * np.log2(p) for p in proportions if p > 0])
        return entropy

    @staticmethod
    def _create_split(X, thresh):
        left_idx = np.argwhere(X <= thresh).flatten()
        right_idx = np.argwhere(X > thresh).flatten()
        return left_idx, right_idx

    def _information_gain(self, X, y, thresh):
        parent_loss = self._entropy(y)
        left_idx, right_idx = self._create_split(X, thresh)
        n, n_left, n_right = len(y), len(left_idx), len(right_idx)

        if n_left == 0 or n_right == 0:
            return 0

        child_loss = (n_left / n) * self._entropy(y[left_idx]) + (n_right / n) * self._entropy(y[right_idx])
        return parent_loss - child_loss

    def _best_split(self, X, y, features):
        split = {'score': - 1, 'feat': None, 'thresh': None}


        for feat in features:
            X_feat = X[:, feat]
            thresholds = np.unique(X_feat)
            for thresh in thresholds:


                score = self._information_gain(X_feat, y, thresh)

                if score > split['score']:
                    split['score'] = score
                    split['feat'] = feat
                    split['thresh'] = thresh

        return split['feat'], split['thresh']

    def _build_tree(self, X, y, depth=0):
        self.n_samples, self.n_features = X.shape
        self.n_class_labels = len(np.unique(y))

        if self._is_finished(depth):
            most_common_Label = np.argmax(np.bincount(y.astype(int)))
            return Node(value=most_common_Label)

        rnd_feats = np.random.choice(self.n_features, self.n_features, replace=False)
        best_feat, best_thresh = self._best_split(X, y, rnd_feats)

        left_idx, right_idx = self._create_split(X[:, best_feat], best_thresh)

        if len(left_idx) == 0 or len(right_idx) == 0:
            most_common_Label = np.argmax(np.bincount(y.astype(int)))
            return Node(value=most_common_Label)
        else:
            left_child = self._build_tree(X[left_idx, :], y[left_idx], depth + 1)
            right_child = self._build_tree(X[right_idx, :], y[right_idx], depth + 1)
            return Node(best_feat, best_thresh, left_child, right_child)

    def _traverse_tree(self, x, node):
        if node.is_leaf():
            return node.value

        if x[node.feature] <= node.threshold:
            return self._traverse_tree(x, node.left)
        return self._traverse_tree(x, node.right)

    def fit(self, X, y, sample_weight=None):
        self.sample_weight = sample_weight
        self.root = self._build_tree(X, y)

    def predict(self, X):
        predictions = [self._traverse_tree(x, self.root) for x in X]
        return np.array(predictions)
