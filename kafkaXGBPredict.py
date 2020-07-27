from kafka import KafkaConsumer
from json import loads
import pickle
from xgboost import XGBClassifier
import xgboost
import pandas as pd

ageEnc = pickle.load(open('./ModelsNEncoders/XGB-LabelEnc_age.pkl','rb'))
genderEnc = pickle.load(open('./ModelsNEncoders/XGB-LabelEnc_gender.pkl','rb'))
categoryEnc = pickle.load(open('./ModelsNEncoders/XGB-LabelEnc_category.pkl','rb'))

print(ageEnc.classes_)
print(genderEnc.classes_)
print(categoryEnc.classes_)

model = pickle.load(open('./ModelsNEncoders/XGB-BankSim.pkl','rb'))

consumer = KafkaConsumer('bank-sim-enh-trans-output',
     bootstrap_servers=['localhost:9092'],
     value_deserializer=lambda x: loads(x.decode('utf-8')))
print('Subscribing to topic: bank-sim-enh-trans-output')
for x in consumer:
     # print(x.value)
     xVal = x.value

     
     idx = xVal['idx']
     fraud = xVal['fraud']

     del xVal['idx']
     del xVal['step']
     del xVal['customer']
     del xVal['merchant']
     del xVal['zipcodeOri']
     del xVal['zipMerchant']
     del xVal['fraud']

     # print('22222')
     row = pd.DataFrame(xVal, index=[0])
     # print('33333')
     # print()
     row['gender'] = genderEnc.transform(row['gender'])
     row['age'] = ageEnc.transform(row['age'])
     row['category'] = categoryEnc.transform(row['category'])
     # print(row)
     pred = model.predict(row)
     if fraud == 1 or pred[0] == 1:
          print(idx,fraud,pred[0])
     else:
          print(idx)