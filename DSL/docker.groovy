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