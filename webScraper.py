#!/usr/bin/env python
# coding: utf-8

# # CS461 Project

# ## Imports

# In[1]:


print("ZERO IQ AI")
print("Importing necessary libraries.")
import json
import argparse
import urllib.request
import re
from urllib.parse import quote
import bs4 as bs
import nltk
from gensim.models import Word2Vec
import numpy as np
from nltk.stem import PorterStemmer  
from nltk.corpus import stopwords
from gensim.models import KeyedVectors
from html import unescape
import torch
from pytorch_pretrained_bert import BertTokenizer, BertModel, BertForMaskedLM


# In[2]:


def cleanString(str):
    ans = str.lower()
    ans = re.sub('[^a-zA-Z]', ' ', ans )
    ans = re.sub(r'\s+', ' ', ans)
    return ans


# In[3]:


#cleanString("sda adsdsa")


# ## Merriam-Webster Scraper

# In[71]:


keyfile1 = open("collegiate.txt", 'r')
keyfile2 = open("learner.txt", 'r')
keys1 = keyfile1.readline()
keys2 = keyfile2.readline()
keyfile1.close()
keyfile2.close()
def dictionarySearch(query):
    query = query.lower()
    urlfrmt1 = "https://dictionaryapi.com/api/v3/references/collegiate/json/"+quote(query)+"?key=" + keys1
    urlfrmt2 = "https://dictionaryapi.com/api/v3/references/learners/json/"+quote(query)+"?key=" + keys2
    response1 = urllib.request.urlopen(urlfrmt1)
    jsStruct1 = json.load(response1)
    response2 = urllib.request.urlopen(urlfrmt2)
    jsStruct2 = json.load(response2)

    all_definitions = []
    for meaning in jsStruct1+jsStruct2:
        try:
            word = meaning['meta']['id']
            if (word.count(':') != 0 and word[:word.find(':')].lower() == query) or word.lower() == query:
                #print("I am here")
                definitions = meaning['shortdef']
                all_definitions += definitions
        except TypeError:
            break
        except KeyError:
            pass
    
    ps = PorterStemmer()   
    all_definitions = [e for e in all_definitions if not query in e] 
    all_definitions = [e for e in all_definitions if not ps.stem(query) in e] 
    
    examples = []

    for usage in jsStruct1+jsStruct2:
        try:
            longdefs = usage['def']
            for i, eachDef in enumerate(longdefs, 1):
                if 'sseq' in eachDef:
                    # test[*][0][1]['dt'] - gives list of illustrations
                    for ill in eachDef['sseq']:
                        if 'dt' in ill[0][1]:
                            for illus in ill[0][1]['dt']:
                                if illus[0] == 'vis':
                                    # enumerate
                                    for ii in range(1, len(illus)):
                                        L = len(illus[ii])
                                        for jj in range(L):
                                            if 't' in illus[ii][jj]:
                                                # remove everything within braces before printing
                                                text = re.sub(r"{.*?}", "", illus[ii][jj]['t'])
                                                if(text.count(query) == 1):
                                                    examples.append(text)
        except KeyError:
            pass
        except TypeError:
            break
    included_words = []
    for sentence in examples:
        if len(sentence.split()) == 1: # if it's a word
            included_words.append(sentence)
    
    for sentence in included_words:
        examples.remove(sentence)
    
    print(query.upper(), " : Definitions = ", all_definitions, "\n\n" + query.upper(), " : Example Sentences = ", examples, "\n\n" + query.upper(), " : Words that include the answer = ", included_words, "\n\n")
    return (all_definitions, examples, included_words)
            


# In[72]:


#x = dictionarySearch("blue") #(definitions, examples)
#x


# ## Clue Comparator

# To make sure the clues are changed

# In[116]:


def compareSentences(query, clue, sentence):
    ps = PorterStemmer()
    keywords = [cleanString(sentence).split(), cleanString(clue).split()]
    keywords[0] = [ps.stem(word) for word in keywords[0] if not word in stopwords.words('english')]
    keywords[0] = np.unique(keywords[0])
    keywords[1] = [ps.stem(word) for word in keywords[1] if not word in stopwords.words('english')]
    keywords[1] = np.unique(keywords[1])
    intersection = len([word for word in keywords[1] if word in keywords[0]])
    if(min(len(keywords[0]),len(keywords[1])) == 0):
        return 1
    ans = intersection / min(len(keywords[0]),len(keywords[1])) < 0.5
    if not ans:
        print(query.upper(), " : Too similar to the old clue. Passing ("+sentence+")")
    return ans


# In[74]:


#compareSentences("Ring-shaped reef", "an island that is made of coral and shaped like a ring")


# ## Example Sentence Scraper

# In[75]:


def scrapeWordHippo(query):
    scrapped_data = urllib.request.urlopen('https://www.wordhippo.com/what-is/sentences-with-the-word/'+quote(query.lower())+'.html')
    article = scrapped_data.read()

    parsed_article = bs.BeautifulSoup(article,'lxml')
    
    ans = []
    for e in parsed_article.find('table', id='mainsentencestable').findAll('td', id=re.compile(r'exv2st.*')):
        ans.append(e.get_text())
    ps = PorterStemmer()  
    ans = [sentence for sentence in ans if sentence.count(ps.stem(query)) == 1] # don't take if there are more than 1 query, it'll confuse BERT
    if len(ans) > 1:
        print(query.upper(), " : More Examples = [", ans[0], ",", ans[1], ", ...]\n\n")
    else:
        print(query.upper(), " : More Examples = ", ans, "\n\n")
    return ans


# In[76]:


#examples = scrapeWordHippo("blue") 
#examples


# ## Example Sentence Picker Using BERT

# In[77]:


# load pretrained bert model
print("Loading pretrained neural network BERT.")
bert_model = BertForMaskedLM.from_pretrained('bert-base-uncased')
bert_model.eval()
print("Model loaded")


# In[78]:


def getProbability(text, word):
    text = text.replace("[=", ". ").replace("]", "")
    
    text = "[CLS] " + re.sub(r"[^ ]*"+word+r"[^ ]*", "[MASK]", text) + " [SEP]"
    if(text == "[CLS] [MASK] [SEP]"):
        return 0
    #print(text)
    # Load pre-trained model tokenizer (vocabulary)
    tokenizer = BertTokenizer.from_pretrained('bert-base-uncased', do_lower_case=True, do_basic_tokenize=True)
    tokenized_text = tokenizer.tokenize(text)
    indexed_tokens = tokenizer.convert_tokens_to_ids(tokenized_text)

    # Create the segments tensors.
    segments_ids = [0] * len(tokenized_text)

    # Convert inputs to PyTorch tensors
    tokens_tensor = torch.tensor([indexed_tokens])
    segments_tensors = torch.tensor([segments_ids])

    # Predict all tokens
    with torch.no_grad():
        predictions = bert_model(tokens_tensor, segments_tensors)

    masked_index = tokenized_text.index('[MASK]')
    pp = torch.nn.functional.softmax(predictions, -1)
#    (prob, item) = torch.topk(pp[0, masked_index],k=1000)
#    top_ten = []
#    for i in range(len(item)):
#        top_ten.append((tokenizer.convert_ids_to_tokens([item[i].item()])[0], float(prob[i])))
#    print(top_ten)
    prob = 0.0
    for e in tokenizer.convert_tokens_to_ids(tokenizer.tokenize(word)):
        prob = max(pp[0,masked_index][e], prob)
    return float(prob)


# In[79]:


#print(getProbability("Beetroot is also great for making soup and can be sliced, diced or grated to add vivid colour to salads.", "diced"))


# His reward was the monastery's sanctus bell which had a blue clapper.
# His reward was the monastery's sanctus bell which had a [MASK] clapper.
# red %7
# blue %6

# In[113]:


def getBestSentence(examples, word, clue):
    best_sentence = ""
    best_probability = 0
    if(" " in word):
        return("", 0)
    #print(examples)
    for e in examples:
        if len(e.split())==1:
            continue
        if not compareSentences(word, clue, e):
            continue
            
        print(word.upper(), " : Evaluating example sentence ("+e+")")
        prob = getProbability(e, word)
        print(word.upper(), " : Probability of guessing the word: " + str(int(prob*100)) + "%\n\n")
        
        
        if prob > best_probability:
            best_sentence = e
            best_probability = prob
    return (best_sentence.replace(word, "_____"), best_probability)


# In[81]:


#getBestSentence(x[1]+examples, "blue", "")


# ## Google Knowledge Graph API

# In[117]:


def googleKnowledgeGraph(query):
    api_key = "AIzaSyAIDMUIhQz2f8E_bTMUXkLgUH6SEZjTy8Y"
    service_url = 'https://kgsearch.googleapis.com/v1/entities:search'
    params = {
        'query': query,
        'limit': 10,
        'indent': True,
        'key': api_key,
    }
    url = service_url + '?' + urllib.parse.urlencode(params)
    response = json.loads(urllib.request.urlopen(url).read())
    #print(response['itemListElement'])
    meanings = []
    ps = PorterStemmer() 
    for element in response['itemListElement']:
        name = unescape(element['result']['name'])
        try:
            details = unescape(element['result']['detailedDescription']['articleBody']).split(".")[0]
            URL = unescape(element['result']['detailedDescription']['url'])
        except KeyError:
            details = ''
            URL = ''
            pass
        resultType = unescape(element['result']['@type'])
        if name.lower().count(query) == 1:
            s_name = ''
            if name.lower() != query.lower():
                if "Person" in resultType and name.split()[-1].lower() == query:
                    s_name = "Last name of " + re.sub(r"(?i) " + query, "", name)+", "
                elif "Person" in resultType and name.split()[0].lower() == query:
                    s_name = "First name of " + re.sub(r"(?i)" + query + " ", "", name)+", "
                else:
                    s_name = re.sub(r"(?i)" + query, "_____", name) + ", "
            try:
                meaning = s_name + unescape(element['result']['description'])
                if not query in meaning.lower() and not ps.stem(query) in meaning.lower():
                    meanings.append(meaning)
            except KeyError:
                pass
            #print(details.lower()[:details.find("is")])
            s_details = ''
            if details.lower()[:details.find("is")-1] == name.lower():
                s_details = (details[details.index("is")+3:])
            elif details.lower().count(query) == 1:
                s_details = (re.sub(r"(?i)" + query, "_____", details))
            elif query not in details.lower():
                s_details = (details)
            if("_____" in s_name and "_____" in s_details):
                if not query in s_details.lower() and not ps.stem(query) in s_details.lower():
                    meanings.append(s_details)
            elif details != '':
                if not query in (s_name + s_details).lower() and not ps.stem(query) in (s_name + s_details).lower():
                    meanings.append(s_name + s_details)
            #print(URL, "\n")
    print(query.upper(), " : Data from Google Knowledge Graph = ", meanings,"\n\n")
    return meanings


# In[83]:


#googleKnowledgeGraph("dys")


# ## Definition Sentence Picker

# Compares the sum of sentence vector with the original word using cosine similarity

# In[137]:


# load pretrained word vectors
print("Loading pretrained word vector dataset.")
word_vector_model = KeyedVectors.load_word2vec_format('GoogleNews-vectors-negative300.bin', binary=True, limit=10 ** 6) 
print("Dataset loaded.")
# 8GB ram wasn't enough to load all. Delete limit if ram>8GB


# In[125]:


def findBestDefinition(query, definition_list, clue):
    best_definition = ""
    best_similarity = 0
    
    query_words = query.split()
    if not query in stopwords.words('english'):
        query_words = [word for word in query_words if not word in stopwords.words('english')]
    
    if definition_list == []:
        print(query.upper(), " : No definition available to evaluate.")
        return (best_definition, best_similarity)
    
    for word in query_words:
        if not word in word_vector_model:
            print(query.upper(), " : Word isn't in the dataset. Cannot evaluate definitions.")
            best_definition = max(definition_list, key=len)
            best_similarity = -1
            return (best_definition, best_similarity)
    
    for sentence in definition_list:
        if not compareSentences(query, clue, sentence):
            continue
        
        print(query.upper(), " : Evaluating definition ("+sentence+")")
        
        search_words = cleanString(sentence).split()

        sentence_clear = [word for word in search_words if not word in stopwords.words('english')]
        sentence_clear = np.unique(sentence_clear)
        total_words = len(sentence_clear)
        sentence_clear = [word for word in sentence_clear if word in word_vector_model.vocab.keys()]
        if(total_words == 0):
            return ('', 0)
        percentage = len(sentence_clear) / total_words #if all the words aren't present in vocab, penalize
        u = np.zeros(300)

        for word in sentence_clear:
            u += word_vector_model[word]
        v = np.zeros(300)
        for word in query_words:
            v += word_vector_model[word]
        if(np.linalg.norm(u) == 0.0):
            print(query.upper(), " : Failed to evaluate definition\n\n")
            continue
        similarity = percentage*np.dot(u, v)/np.linalg.norm(u)/np.linalg.norm(v)
        if best_similarity < similarity:
            best_definition = sentence
            best_similarity = similarity
        print(query.upper(), " : Similarity is:"+str(int(similarity*100))+"\n\n")
    return (best_definition, best_similarity)


# In[86]:


#findBestDefinition("kind of", x[0])


# In[87]:


#s = ['Song by Cool Hand Luke', "First name of Fishman, Hal Fishman's wife", "Noel '_____' Minor, Fictional character"]


# In[88]:


#findBestDefinition("wang", s)


# ## Prefix Suffix Finder

# In[89]:


def prefixSuffixFinder(example_words, query, clue):
    total_results = []
    for word in example_words:
        if(word.index(query) == 0):
            if not word[len(query):] in clue:
                total_results.append("Prefix with -" + word[len(query):])
        elif(word.index(query) == len(word) - len(query)):
            if not word[:word.index(query)] in clue:
                total_results.append("Suffix with " + word[:word.index(query)] + "-")
    return total_results


# In[90]:


#prefixSuffixFinder(['dyslexia', 'dysplasia', 'dysphagia', 'dysfunction', 'dyslogistic'], "dys", "Prefix with -lexia")


# In[91]:


#a = "lexiadys"
#print(a.index("dys") == len(a) - len("dys"))
#a[:a.index("dys")]


# # Main Wrapper

# In[133]:


def getBestChoice(query, clue):
    meaning_threshold = 0
    example_threshold = 0.5
    GKG_threshold = 0.1
    
    ans = []
    print(query.upper(), " : Getting dictionary data")
    dictionary_data = dictionarySearch(query)
    print(query.upper(), " : Evaluating definitions")
    best_def = findBestDefinition(query, dictionary_data[0], clue)
    if best_def[0] != '' and best_def[1] > meaning_threshold:
        print(query.upper(), " : Best definition is selected as clue (" + best_def[0] + ")\n\n\n---------------------------------\n\n\n")
        return best_def[0]
    
    print(query.upper(), " : Scraping example sentences")
    examples_word_hippo = []
    try:
        examples_word_hippo = scrapeWordHippo(query)
    except AttributeError:
        pass
    
    examples = examples_word_hippo + dictionary_data[1]
    print(query.upper(), " : Evaluating example sentences")
    best_example = getBestSentence(examples, query, clue)
    #print(best_example)
    #print("best example selected")
    
    if best_example[0] != '' and best_example[1] > example_threshold:
        print(query.upper(), " : Most guessable example sentence is selected as clue (" + best_def[0] + ")\n\n\n---------------------------------\n\n\n")
        return best_example[0]
    #print("google knowledge graph API")
    print(query.upper(), " : Scraping Google Knowledge Graph")
    google_knowledge = googleKnowledgeGraph(query)
    print(query.upper(), " : Evaluating data")
    best_knowledge = findBestDefinition(query, google_knowledge, clue)
    
    if best_knowledge[0] != '' and best_knowledge[1] > GKG_threshold:
        print(query.upper(), " : Best Google Knowledge Graph output is selected as clue (" + google_knowledge[0] + ")\n\n\n---------------------------------\n\n\n")
        return(google_knowledge[0])
    
    prefix_suffix = prefixSuffixFinder(dictionary_data[2], query, clue)
    if prefix_suffix != [] and compareSentences(query, clue, prefix_suffix[0]):
        print(query.upper(), " : Word will be described as a part of a bigger word (" + prefix_suffix[0] + ")\n\n\n---------------------------------\n\n\n")
        return prefix_suffix[0]
    
    if best_knowledge[0] != '' and best_knowledge[1] == -1 and compareSentences(query, clue, google_knowledge[0]):
        print(query.upper(), " : A random Google Knowledge Graph output is selected as clue (" + google_knowledge[0] + ")\n\n\n---------------------------------\n\n\n")
        return(google_knowledge[0])
    return ''


# In[134]:


#getBestChoice("notre", "") # meaning similarity 2 word


# In[135]:


def getBestChoice2(word, clue):
    ans = getBestChoice(word,clue)
    if ans != '':
        return ans
    print(word.upper(), " : Clue couldn't be generated. Trying to evaluate the stem of the word.")
    ps = PorterStemmer()  
    ans = getBestChoice(ps.stem(word),clue)
    if ans != '':
        print(word.upper(), " : Using the stem of the word to decribe it.")
        return "Derived from the word that is decribed by the following: " + ans
    
    print(word.upper(), " : Clue couldn't be generated from the stem of the word either. Checking whether the word is actually multiple words combined.")
    
    url = "https://api.datamuse.com/sug?s=" + quote(word)
    response = json.loads(urllib.request.urlopen(url).read())
    for e in response:
        try:
            if ''.join(e["word"].split()) == word and e["word"] != word:
                print(word.upper(), " : Word is changed into:", e["word"])
                return getBestChoice(e["word"],clue)
        except KeyError:
            pass
    
    print(word.upper(), " : Word couldn't be divided or no definition found. Trying to use a rhyming word for the clue")

    
    url = "https://api.datamuse.com/words?rel_rhy=" + quote(word)
    response = json.loads(urllib.request.urlopen(url).read())
    try:
        print(word.upper(), " : Found a rhyming word ("+response[0]["word"]+")\n\n\n---------------------------------\n\n\n")
        return '"It rhymes with '+ response[0]["word"] +'," for this answer'
    except KeyError:
        pass

    print(word.upper(), " : Clue generation failed.")

    return 'Failed to generate clue'


# In[127]:


#getBestChoice2("notre", "University of ___ Dame")


# In[ ]:


puzzle = open("aiFood.txt")
text = puzzle.readlines()
puzzle.close()
words = re.sub(r"[^A-Za-z,]", '', text[1]).split(",")
#print(words)
clues = text[2][2:-3].replace("', '", '", "').replace("', \"", '", "').replace("\", '", '", "').split('", "')
for i in range(10):
    clues[i] = clues[i]
#print(clues)
#words = ['DIP', 'VIRAL', 'ECOLI', 'TENET', 'DYS', 'DICED', 'IRONY', 'PALES', 'VET', 'LIT']
new_clues = []
print("Starting clue generation.")
for i in range(10):
    word = words[i].lower()
    clue = clues[i]
    new_clues.append(getBestChoice2(word,clue))
print(new_clues)
file1 = open("generatedClues.txt","w+") 
file1.write("#####".join(new_clues)) 
file1.close()
#np.savetxt("generatedClues.txt", new_clues, delimiter=",", fmt="%s")
print("Clue generation completed.")
input()


# In[ ]:




