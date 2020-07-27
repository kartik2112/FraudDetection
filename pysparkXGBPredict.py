# To execute, use
# .\bin\spark-submit --master local[4] --packages org.apache.spark:spark-sql-kafka-0-10_2.12:3.0.0 "E:\OneDrive\Projects\Banking Mule Detection Research\Kafka Test Codes\pysparkXGBPredict.py"

from pyspark import SparkConf, SparkContext
from pyspark.sql import SQLContext, SparkSession
from pyspark.sql.types import *
from pyspark.sql.functions import udf, struct, collect_list, col, pandas_udf, PandasUDFType
import json
import sys
sys.path.insert(0, ['C:\\ProgramData\\Anaconda3\\DLLs', 'C:\\ProgramData\\Anaconda3\\lib', 'C:\\ProgramData\\Anaconda3', 'C:\\ProgramData\\Anaconda3\\lib\\site-packages', 'C:\\ProgramData\\Anaconda3\\lib\\site-packages\\win32', 'C:\\ProgramData\\Anaconda3\\lib\\site-packages\\win32\\lib', 'C:\\ProgramData\\Anaconda3\\lib\\site-packages\\Pythonwin'])

import pickle
from xgboost import XGBClassifier
import xgboost
import pandas as pd

dirPath = "E:\OneDrive\Projects\Banking Mule Detection Research\FraudDetection\ModelsNEncoders"

ageEnc = pickle.load(open(dirPath + '\XGB-LabelEnc_age.pkl','rb'))
genderEnc = pickle.load(open(dirPath + '\XGB-LabelEnc_gender.pkl','rb'))
categoryEnc = pickle.load(open(dirPath + '\XGB-LabelEnc_category.pkl','rb'))

print(ageEnc.classes_)
print(genderEnc.classes_)
print(categoryEnc.classes_)

model = pickle.load(open(dirPath + '\XGB-BankSim.pkl','rb'))

# conf = SparkConf().setAppName("applicaiton") \
#     .set("spark.executor.heartbeatInterval", "1s") \
#     .set("spark.network.timeout", "500s") 
#     # spark.network.timeout 

# sc = SparkContext.getOrCreate(conf)
# spark = SQLContext(sc).sparkSession

print('Loaded XGB Models and Encoders')

spark = SparkSession.builder.appName('BankSimOPConsumer').getOrCreate()

# Added these 2 lines because, I was getting the error of memory leak when publishing the streaming dataframe to Kafka topic
# https://github.com/arctern-io/arctern/issues/399#issuecomment-629573958
spark.conf.set("spark.sql.execution.arrow.pyspark.enabled", "true")
spark.conf.set("spark.sql.execution.arrow.maxRecordsPerBatch", "500000")

# spark.conf.set("spark.executor.heartbeatInterval","60s")

df = spark.readStream.format("kafka") \
    .option("kafka.bootstrap.servers", "localhost:9092") \
    .option("subscribe", "bank-sim-enh-trans-output") \
    .option("kafkaConsumer.pollTimeoutMs","50000") \
    .load()

df.printSchema()
# This returned
# root
#  |-- key: binary (nullable = true)
#  |-- value: binary (nullable = true)
#  |-- topic: string (nullable = true)
#  |-- partition: integer (nullable = true)
#  |-- offset: long (nullable = true)
#  |-- timestamp: timestamp (nullable = true)
#  |-- timestampType: integer (nullable = true)



# Convert the binary value to spring
tempDF = df.selectExpr("CAST(key AS STRING)","CAST(value as STRING)")
tempDF.printSchema()
# This returned
# root
#  |-- key: string (nullable = true)
#  |-- value: string (nullable = true)





fields = [StructField("idx", LongType(), True),
          StructField("step", LongType(), True),
          StructField("customer", StringType(), True),
          StructField("age", StringType(), True),
          StructField("gender", LongType(), True),
          StructField("zipcodeOri", StringType(), True),
          StructField("merchant", StringType(), True),
          StructField("zipMerchant", StringType(), True),
          StructField("category", StringType(), True),
          StructField("amount", DoubleType(), True),
          StructField("fraud", IntegerType(), True),
          StructField("count_1_day", LongType(), True),
          StructField("count_7_days", LongType(), True),
          StructField("count_30_days", LongType(), True),
          StructField("count_cust_merch_1_day", LongType(), True),
          StructField("count_cust_merch_7_days", LongType(), True),
          StructField("count_cust_merch_30_days", LongType(), True)]
schema1 = StructType(fields)

# fieldNames = ["idx", ]

def parse_json(json_str):
    return json.loads(json_str)

def serialize_json(json_obj):
    return json.dumps(json_obj)


@pandas_udf('int', PandasUDFType.SCALAR)
def agePandasUDF(x):
    return pd.Series(ageEnc.transform(x)).astype(int)

@pandas_udf('int', PandasUDFType.SCALAR)
def genderPandasUDF(x):
    return pd.Series([2]).repeat(len(x))
    # return pd.Series(genderEnc.transform(x)).astype(str)

@pandas_udf('int', PandasUDFType.SCALAR)
def categoryPandasUDF(x):
    return pd.Series(categoryEnc.transform(x)).astype(int)

@pandas_udf('preds int')
def predictPandasUDF(col1:pd.Series, x:pd.DataFrame) -> pd.DataFrame:
    return pd.DataFrame({'preds': model.predict(x)})
    # return pd.DataFrame({'preds': pd.Series(json.dumps(list(x.columns))).repeat(len(x))})

udf_parse_json = udf(lambda str: parse_json(str), schema1)
udf_serialize_json = udf(lambda str: serialize_json(str), StringType())

# ageUDF = udf(lambda x: ageEnc.transform(x), StringType())
# genderUDF = udf(lambda x: genderEnc.transform(x), StringType())
# categoryUDF = udf(lambda x: categoryEnc.transform(x), StringType())

# predictUDF = udf(lambda x: model.predict(x), LongType())

transDF = tempDF.select(tempDF.key, udf_parse_json(tempDF.value).alias("value"))
transDF.printSchema()
# This returned
# root
#  |-- key: string (nullable = true)
#  |-- value: struct (nullable = true)
#  |    |-- idx: long (nullable = true)
#  |    |-- step: long (nullable = true)
#  |    |-- customer: string (nullable = true)
#  |    |-- age: string (nullable = true)
#  |    |-- gender: long (nullable = true)
#  |    |-- zipcodeOri: string (nullable = true)
#  |    |-- merchant: string (nullable = true)
#  |    |-- zipMerchant: string (nullable = true)
#  |    |-- category: string (nullable = true)
#  |    |-- amount: double (nullable = true)
#  |    |-- fraud: integer (nullable = true)
#  |    |-- count_1_day: long (nullable = true)
#  |    |-- count_7_days: long (nullable = true)
#  |    |-- count_30_days: long (nullable = true)
#  |    |-- count_cust_merch_1_day: long (nullable = true)
#  |    |-- count_cust_merch_7_days: long (nullable = true)
#  |    |-- count_cust_merch_30_days: long (nullable = true)




transDF = transDF.select(transDF.key,
                        struct(
                            agePandasUDF(col("value.age")).alias('age'), 
                            genderPandasUDF(col("value.gender")).alias('gender'),
                            categoryPandasUDF(col("value.category")).alias('category'),
                            transDF.value.amount.alias('amount'), 
                            transDF.value.count_1_day.alias('count_1_day'), transDF.value.count_7_days.alias('count_7_days'), transDF.value.count_30_days.alias('count_30_days'),
                            transDF.value.count_cust_merch_1_day.alias('count_cust_merch_1_day'), transDF.value.count_cust_merch_7_days.alias('count_cust_merch_7_days'), transDF.value.count_cust_merch_30_days.alias('count_cust_merch_30_days'))
                        .alias('value'),
                        transDF.value.fraud.alias('fraudOG'))
transDF.printSchema()
# This returned
# root
#  |-- key: string (nullable = true)
#  |-- value: struct (nullable = false)
#  |    |-- age: integer (nullable = true)
#  |    |-- gender: integer (nullable = true)
#  |    |-- category: integer (nullable = true)
#  |    |-- amount: double (nullable = true)
#  |    |-- count_1_day: long (nullable = true)
#  |    |-- count_7_days: long (nullable = true)
#  |    |-- count_30_days: long (nullable = true)
#  |    |-- count_cust_merch_1_day: long (nullable = true)
#  |    |-- count_cust_merch_7_days: long (nullable = true)
#  |    |-- count_cust_merch_30_days: long (nullable = true)
#  |-- fraudOG: integer (nullable = true)



transDF = transDF.select(transDF.key,transDF.fraudOG,predictPandasUDF(transDF.key,transDF.value).preds.alias('preds'))
transDF.printSchema()
# This returned
# root
#  |-- key: string (nullable = true)
#  |-- fraudOG: integer (nullable = true)
#  |-- preds: integer (nullable = true)


transDF = transDF.select(transDF.key,udf_serialize_json(struct('key','fraudOG','preds')).alias('value'))
transDF.printSchema()
# This returned
# root
#  |-- key: string (nullable = true)
#  |-- value: string (nullable = true)


# transDF = transDF.groupby().count()


# transDF.show()

    # transDF \
# try:
#     transDF.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)") \
#         .writeStream \
#         .format("kafka") \
#         .option("spark.kafka.producer.cache.timeout", "5m") \
#         .option("checkpointLocation", "/tmp/logs/spark/BankSimOPConsumer") \
#         .option("kafka.bootstrap.servers", "localhost:9092") \
#         .option("topic", "predictionsOP") \
#         .start() \
#         .awaitTermination()
# except:
#     print('Exception caught')

transDF.writeStream \
    .format("console") \
    .start() \
    .awaitTermination()