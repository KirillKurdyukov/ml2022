// because kotlin or java don't have long double
#include <iostream>
#include <vector>
#include <unordered_set>
#include <unordered_map>

class NaiveBayesClassifier {

private:
    int n, k, alpha, q = 2;
    std::vector<int> lambdas;
    std::unordered_set<std::string> words;
    std::vector<int> classSize;
    std::vector<std::unordered_map<std::string, int>> classWordCount;
    std::vector<std::unordered_map<std::string, long double>> classWordProbability;
public:
    NaiveBayesClassifier(int n, int k, std::vector<int> lambdas, int alpha) : n(n), k(k), alpha(alpha),
                                                                              lambdas(std::move(lambdas)) {
        classWordCount.resize(k);
        classWordProbability.resize(k);
        classSize.resize(k);
    }

    void fit(std::vector<std::pair<int, std::unordered_set<std::string>>> &df) {
        std::for_each(df.begin(), df.end(), [&](const std::pair<int, std::unordered_set<std::string>> &it) {
            classSize[it.first]++;
            std::for_each(it.second.begin(), it.second.end(), [&](const auto &item) {
                words.insert(item);
                classWordCount[it.first][item]++;
            });
        });

        for (int i = 0; i < k; i++) {
            for (auto &[word, cnt]: classWordCount[i]) {
                classWordProbability[i][word] = ((long double) cnt + alpha) / (q * alpha + classSize[i]);
            }
        }
    }

    std::vector<long double> predict(std::unordered_set<std::string> message) {
        long double sumProp = 0.0;
        std::vector<long double> result;
        for (int i = 0; i < k; i++) {
            long double prob = (long double) classSize[i] * lambdas[i] / n;
            std::for_each(words.begin(), words.end(), [&](auto &item) {
                long double cur = (classWordProbability[i].find(item) == classWordProbability[i].end()) ?
                                  (long double) alpha / (classSize[i] + q * alpha) : classWordProbability[i][item];
                prob *= (message.find(item) == message.end()) ? (1 - cur) : cur;
            });
            result.push_back(prob);
            sumProp += prob;
        }

        std::for_each(result.begin(), result.end(), [&](auto &item) {
            item /= sumProp;
        });
        return result;
    }
};

int main() {
    int k;
    std::cin >> k;
    std::vector<int> lambdas(k);
    for (int i = 0; i < k; i++) {
        std::cin >> lambdas[i];
    }
    int alpha, n;
    std::cin >> alpha >> n;
    NaiveBayesClassifier classifier(n, k, lambdas, alpha);
    std::vector<std::pair<int, std::unordered_set<std::string>>> df;
    for (int i = 0; i < n; i++) {
        int cl, size;
        std::cin >> cl >> size;
        std::unordered_set<std::string> words;
        for (int j = 0; j < size; j++) {
            std::string word;
            std::cin >> word;
            words.insert(word);
        }
        df.emplace_back(cl - 1, words);
    }
    classifier.fit(df);
    int m;
    std::cin >> m;
    for (int i = 0; i < m; i++) {
        int size;
        std::cin >> size;
        std::unordered_set<std::string> words;
        for (int j = 0; j < size; j++) {
            std::string word;
            std::cin >> word;
            words.insert(word);
        }
        std::vector<long double> ans = classifier.predict(words);
        for (int j = 0; j < k; j++) std::cout << ans[j] << " ";
        std::cout << '\n';
    }
}
