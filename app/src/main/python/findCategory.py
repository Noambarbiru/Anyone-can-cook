# import sys
# import subprocess
# import pip

# implement pip as a subprocess:
# pip.main(["install", "-U", "sentence-transformers"])
# subprocess.check_call([sys.executable, 'pip', 'install', ''])
import numpy as np
from sentence_transformers import SentenceTransformer
from sklearn.metrics.pairwise import cosine_similarity
import pickle
import time

sentences = [
    "Next",
    "Previews",
    "Repeat",
    "I have a question"
]


# model = SentenceTransformer('bert-base-nli-mean-tokens')
# model.save("model")
# pickle.dump(model, open("bert_model", "wb"))
# model = pickle.load(open("bert_model", "rb"))
# model = SentenceTransformer("model")
# sentence_embeddings = model.encode(sentences)
# pickle.dump(sentence_embeddings, open("categories", "wb"))
# file = open("categories", "wb")
# # save array to the file
# np.save(file, sentence_embeddings)
# # close the file
# file.close()
# file = open("categories", "rb")
# read the file to numpy array
# sentence_embeddings = np.load(file)
# close the file
# file.close()
# sentence_embeddings = pickle.load(open("categories", "rb"))

def best_match(sent):
    model = SentenceTransformer('bert-base-nli-mean-tokens')
    sentence_embeddings = model.encode(sentences)
    similarity = cosine_similarity([model.encode(sent)], sentence_embeddings)[0]
    return np.argmax(similarity)


if __name__ == '__main__':
    start = time.time()
    print(best_match("again"))
    end = time.time()
    print(end - start)
