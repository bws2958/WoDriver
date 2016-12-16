package com.wodriver;

import android.content.Context;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.cognito.Dataset;
import com.amazonaws.mobileconnectors.cognito.DefaultSyncCallback;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.s3.AmazonS3Client;

/**
 * Created by user on 2016-12-07.
 */

public class ManagerClass {
    CognitoCachingCredentialsProvider credentialsProvider = null;
    CognitoSyncManager syncManager = null;
    AmazonS3Client s3Client = null;
    TransferUtility transferUtility = null;

    public static AmazonDynamoDBClient dynamoDBClient = null;
    public static DynamoDBMapper dynamoDBMapper = null;
    public CognitoCachingCredentialsProvider getcredentials(Context context){
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                context,com.wodriver.util.Constants.Identity_POOL_ID, Regions.AP_NORTHEAST_2);
        syncManager = new CognitoSyncManager(context,Regions.AP_NORTHEAST_2, credentialsProvider);
        Dataset dataset = syncManager.openOrCreateDataset("Mydataset");
        dataset.put("mykey", "myvalue");

        dataset.synchronize(new DefaultSyncCallback());

        return credentialsProvider;
    };

    public AmazonS3Client inits3client(Context context){
        if(credentialsProvider == null){
            getcredentials(context);
            s3Client = new AmazonS3Client(credentialsProvider);
            s3Client.setRegion(Region.getRegion(Regions.AP_NORTHEAST_2));
        }
        return s3Client;
    }

    public TransferUtility checktransferutility(AmazonS3Client s3Client, Context context){
        if(transferUtility == null){
            transferUtility = new TransferUtility(s3Client, context);
            return transferUtility;
        }else{
            return transferUtility;
        }
    }

    public DynamoDBMapper intiDynamoClient(CognitoCachingCredentialsProvider credentialsProvider){
        if(dynamoDBClient == null){
            dynamoDBClient = new AmazonDynamoDBClient(credentialsProvider);
            dynamoDBClient.setRegion(Region.getRegion(Regions.AP_NORTHEAST_2));
            dynamoDBMapper = new DynamoDBMapper(dynamoDBClient);
        }
        return dynamoDBMapper;
    }
}
