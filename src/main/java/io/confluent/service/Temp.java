package io.confluent.service;

import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sts.StsAsyncClient;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;
import software.amazon.awssdk.services.sts.model.StsException;
import software.amazon.awssdk.services.sts.model.AssumeRoleResponse;
import software.amazon.awssdk.services.sts.model.Credentials;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Temp {
    public static void main(String[] args) {
        try {
            loadCredentialsV2();
        } catch (Exception ce) {
            ce.printStackTrace();
        }
    }

    private static AwsCredentialsProvider loadCredentialsV2() throws ExecutionException, InterruptedException {
        ProfileCredentialsProvider devProfile = ProfileCredentialsProvider.builder()
                .profileName("default")
                .build();

        StsAsyncClient stsAsyncClient = StsAsyncClient.builder()
                .credentialsProvider(devProfile)
                .region(Region.US_EAST_1)
                .build();

        AssumeRoleRequest assumeRoleRequest = AssumeRoleRequest.builder()
                .durationSeconds(3600)
                .roleArn("arn:aws:iam::54977047978:role/Block-Dynamo-Role")
                .roleSessionName("CloudWatch2_Session")
                .build();

        Future<AssumeRoleResponse> responseFuture = stsAsyncClient.assumeRole(assumeRoleRequest);
        AssumeRoleResponse response = responseFuture.get();
        Credentials creds = response.credentials();

        System.out.println("Access Key :"+creds.accessKeyId());
        System.out.println("Secret :"+creds.secretAccessKey());
        System.out.println("Session Token :"+creds.sessionToken());
        AwsSessionCredentials sessionCredentials = AwsSessionCredentials.create(creds.accessKeyId(), creds.secretAccessKey(), creds.sessionToken());
        return AwsCredentialsProviderChain.builder()
                .credentialsProviders(StaticCredentialsProvider.create(sessionCredentials))
                .build();
    }
}


