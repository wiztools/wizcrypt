#!/bin/sh

## The doc on how to publish in Maven public repo:
## http://maven.apache.org/guides/mini/guide-ibiblio-upload.html

## The Jira request has to be posted here:
## http://jira.codehaus.org/secure/CreateIssue.jspa?pid=10367&issuetype=3

mvn clean
mvn source:jar javadoc:jar repository:bundle-create

