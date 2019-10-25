import jenkins.model.*
import hudson.model.*;
def jobName = "primer_job"
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

Jenkins.instance.createProjectFromXML(jobName, xmlStream)

jobs = Hudson.instance.getAllItems(FreeStyleProject);

// iterate through the jobs
for (j in jobs) {

  // define a pattern, which jobs I do not want to run
  def pattern = 'trunk';
  def m = j.getName() =~ pattern;

  // if pattern does not match, then run the job
  if (!m) {
    // first check, if job is buildable
    if (j instanceof BuildableItem) {
      // run that job
      j.scheduleBuild();
    }
  }
}