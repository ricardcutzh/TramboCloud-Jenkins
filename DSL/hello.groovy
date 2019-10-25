job('Job1'){
    steps {
        shell("echo 'Hello World'")
    }
}

queue('Job1')