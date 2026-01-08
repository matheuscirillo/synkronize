package io.synkronize.source.aws.sqs;

public record SqsQueueUrl(long validUntil, String queueUrl) {

}
