# Jenkins Docker Exercise
* This excercise builds a Jenkins Image with a seed job and also another job that builds a simple ngnix docker image and pushes it to ECR.

## How to Use It
1. Build Jenkins image with the following command:

```bash
$ docker build --build-arg ACCESS_KEY=[access_key_here] --build-arg SECRET_KEY=[secret_key_here] -t ricardcutzh/jenkins:1.0 .
```

2. Run the jenkins image with the following command>
```bash
$ docker run --rm --name jenkins -e A_KEY=[access_key] -e S_KEY=[secret_key] -p 8080:8080 -p 50000:50000 -v /var/run/docker.sock:/var/run/docker.sock ricardcutzh/jenkins:1.0
```

## Seed Job
* The seed job is the first job that will be created on startup, this job is in charge of creating all the other jobs. The INIT directory contains the groovy script that configures where to execute the other dsl

### example-job.groovy

* Importing and defining a job name:
```groovy
import jenkins.model.*
import hudson.model.*;
def jobName = "primer_job"
```

* Creating XML configuration:
```groovy
def configXml = """
<project>
<description/>
<keepDependencies>false</keepDependencies>
<properties/>
<scm class="hudson.scm.NullSCM"/>
<canRoam>true</canRoam>
<disabled>false</disabled>
<blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>
<blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>
<triggers/>
<concurrentBuild>false</concurrentBuild>
<builders>
<javaposse.jobdsl.plugin.ExecuteDslScripts plugin="job-dsl@1.76">
<targets>*.groovy</targets>
<usingScriptText>false</usingScriptText>
<sandbox>false</sandbox>
<ignoreExisting>false</ignoreExisting>
<ignoreMissingFiles>false</ignoreMissingFiles>
<failOnMissingPlugin>false</failOnMissingPlugin>
<failOnSeedCollision>false</failOnSeedCollision>
<unstableOnDeprecation>false</unstableOnDeprecation>
<removedJobAction>DELETE</removedJobAction>
<removedViewAction>DELETE</removedViewAction>
<removedConfigFilesAction>DELETE</removedConfigFilesAction>
<lookupStrategy>JENKINS_ROOT</lookupStrategy>
</javaposse.jobdsl.plugin.ExecuteDslScripts>
</builders>
<publishers/>
<buildWrappers/>
</project>
""" // your xml goes here

def xmlStream = new ByteArrayInputStream( configXml.getBytes() )
```

### docker.groovy
* This job is in charge of creating a docker image and pushing the image to AWS ECR

* On the jenkins docker image we defined enviromental variables for the AWS CLI

```groovy
job('Docker'){
    steps {
        shell('aws configure set aws_access_key_id $A_KEY')
    }
    steps {
        shell('aws configure set aws_secret_access_key $S_KEY')
    }
    steps {
        shell('docker build -t jenkins-example:latest .')
    }
    steps {
        shell('echo "building finish!"')
    }
    steps {
        shell('$(aws ecr get-login --no-include-email --region us-west-2)')
    }
    steps {
        shell('docker tag jenkins-example:latest 492266378106.dkr.ecr.us-west-2.amazonaws.com/jenkins-example:latest')
    }
    steps {
        shell('docker push 492266378106.dkr.ecr.us-west-2.amazonaws.com/jenkins-example:latest')
    }
    steps {
        shell('echo "finish... build..."')
    }
}

queue('Docker')
```