# FraudDetection

## Architecture

<img src="Images/Architecture Diagram.PNG"/>

## Dependencies

* [Kafka Binary](https://www.apache.org/dyn/closer.cgi?path=/kafka/2.5.0/kafka_2.12-2.5.0.tgz)
* [Cassandra Binary](https://www.apache.org/dyn/closer.lua/cassandra/3.11.7/apache-cassandra-3.11.7-bin.tar.gz)
* [Spark Binary](https://www.apache.org/dyn/closer.lua/spark/spark-3.0.0/spark-3.0.0-bin-hadoop2.7.tgz)

Extract all these libaries using `tar -zxvf filename.tar.gz`

### Python Dependencies

* kafka-python
* numpy
* pandas
* py4j
* pyarrow
* pyspark
* scikit-learn
* thrift
* xgboost

Instead, you could directly execute

```
pip install -r .\requirements.txt
```

## Setup Instructions

* Ensure the above 3 binaries are present in some convenient location such as `C:\BigData\xxxx`. You may or may not add the bin folders in the PATH environment variable. This setup has been tested on Win 10 machine.
* Ensure Python libraries are present in PATH environment variable.
* Ensure all needed python libraries are installed.
* Go to this project's `javaStreamers` folder and execute
    `mvn install`
* Open cassandra instance by navigating to Cassandra binary's directory and executing: `.\bin\cassandra.bat -f`
* In another window, again navigate to Cassandra binary's `bin` directory and execute: `cqlsh`. This will open the CQL prompt of Cassandra. Commands are very similar to SQL.

    Next, execute the following CQL scripts to configure the necessary tables:
    * CREATE KEYSPACE test
        WITH replication = {'class': 'SimpleStrategy', 'replication_factor' : 1};
    * USE test;
    * CREATE TABLE transactions(
        idx BIGINT,
        step BIGINT,
        customer TEXT,
        age TEXT,
        gender TEXT,
        zipcodeOri TEXT,
        merchant TEXT,
        zipMerchant TEXT,
        category TEXT,
        amount DECIMAL,
        fraud INT,
        PRIMARY KEY(customer, step, idx));
    * CREATE MATERIALIZED VIEW transactionsCustMerchs
        AS SELECT * FROM transactions WHERE
        merchant IS NOT NULL AND
        customer IS NOT NULL AND
        step IS NOT NULL AND
        idx IS NOT NULL
        PRIMARY KEY(merchant,customer,step,idx);

## Execution Instructions

1. Command Prompt 1 (Zookeeper Server)
    * Navigate to Kafka directory: `C:\BigData\C:\BigData\kafka_2.12-2.5.0`
    * Open Zookeeper server by executing:
        `.\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties`
1. Command Prompt 2 (Kafka Server)
    * Navigate to Kafka directory: `C:\BigData\C:\BigData\kafka_2.12-2.5.0`
    * Open Kafka server by executing:
        `.\bin\windows\kafka-server-start.bat .\config\server.properties`
1. Python Command Prompt 3 (pysparkXGBPredict)
    
    Ensure that you run this from Anaconda Prompt (if you use conda) or the virtualenv you use.
    
    This 3rd pipeline stage will receive enhanced transactions from Kafka topic `bank-sim-enh-trans-output`, perform encoding operations (using sklearn LabelEncoders used during training), run XGB prediction on these batches of feature vectors and output actual and predicted fraud values for each transaction.
    * Navigate to Spark directory: `C:\BigData\spark-3.0.0-bin-hadoop2.7`
    * Execute 
        `.\bin\spark-submit --master local[4] --packages org.apache.spark:spark-sql-kafka-0-10_2.12:3.0.0 "ABS_FILE_PATH\FraudDetection\pysparkXGBPredict.py"`
1. Command Prompt 4 (CassandraInteract)
    
    This 2nd pipeline stage will receive transactions from Kafka topic `bank-sim-transactions-input1`, adds records to Cassandra DB, executes aggregation queries and sends enhanced transactions to Kafka topic `bank-sim-enh-trans-output`.
    * Navigate to project's `javaStreamers` directory
    * Execute 
        `mvn exec:java -Dexec.mainClass=myapps.CassandraInteract`
1. Command Prompt 5
    
    This 1st pipeline stage will read the csv file: https://github.com/kartik2112/FraudDetection/blob/master/javaStreamers/bs140513_032310.csv and generate a stream of transactions by publishing them to Kafka topic `bank-sim-transactions-input1`
    * Navigate to project's `javaStreamers` directory
    * Execute 
        `mvn exec:java -Dexec.mainClass=myapps.BankSimDataProducer`

## Datasets Used for Simulation

* [BankSim](https://www.kaggle.com/ntnu-testimon/banksim1)

## References

* https://medium.com/@sushantgautam_930/simple-way-to-install-cassandra-in-windows-10-6497e93989e6
* https://docs.datastax.com/en/developer/java-driver/3.0/manual/statements/prepared/
* https://kafka.apache.org/quickstart
* https://changhsinlee.com/pyspark-udf/
* https://medium.com/civis-analytics/prediction-at-scale-with-scikit-learn-and-pyspark-pandas-udfs-51d5ebfb2cd8
* https://spark.apache.org/docs/latest/structured-streaming-kafka-integration.html#deploying
* https://kontext.tech/column/spark/284/pyspark-convert-json-string-column-to-array-of-object-structtype-in-data-frame
