import nltk
import re

class Tokenizer(object):
    
    emoticons = {'##s##': [':)',':-)',';-)',': )',':d','=)',':p',';)','<3',':D'],
                 '##b##': [':(',':-(',': (']
                 }
    
    re_username =  re.compile('@[a-z_]*',re.UNICODE)
    re_url = re.compile("https?://[a-z0-9/.#?=&+,@-_~]*")
    re_plain = re.compile('[^\w\s#]',re.UNICODE)
    re_hash = re.compile('#[\S]*',re.UNICODE)
    re_numbers = re.compile('[0-9]*',re.UNICODE)
    
    def __init__(self):
        self.stopwords = nltk.corpus.stopwords.words('english')
       
    def convert_emoticons(self, text):
        # convert :) to ### and :( to ***
        for label, items in self.emoticons.items():
            for item in items:
                text = text.replace(item, " %s " % label)
         
        return text
    
    def remove_usernames(self, text):
        return self.re_username.sub('', text)
    
    def remove_hashtags(self, text):
        return self.re_hash.sub('', text)
    
    def remove_urls(self, text):
        return self.re_url.sub(' ', text)
    
    def make_plain(self, text):
        return self.re_plain.sub(' ', text)
    
    def remove_numbers(self, text):
        return self.re_numbers.sub('', text)
    
    def remove_stopwords(self, tokens):
        return [word for word in tokens if not word in self.stopwords]

    def clean_text(self, text):
        text = text.lower()
        text = self.remove_usernames(text)
        text = self.remove_hashtags(text)
        text = self.convert_emoticons(text)
        text = self.remove_urls(text)
        text = self.remove_numbers(text)
        text = self.make_plain(text)
        return text.strip()       
    
    def tokenize(self, text):
        text = unicode(text)
        text = self.clean_text(text)
        
        return self.remove_stopwords(text.split())

