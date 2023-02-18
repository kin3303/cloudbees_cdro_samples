pipeline 'University-CI', {
  description = ''
  disableMultipleActiveRuns = '0'
  disableRestart = '0'
  enabled = '1'
  overrideWorkspace = '0'
  projectName = 'CO_WEB_DEMO'
  skipStageMode = 'ENABLED'

  formalParameter 'IssueKey', defaultValue: 'DOP-117', {
    expansionDeferred = '0'
    orderIndex = '1'
    required = '1'
    type = 'entry'
  }

  formalParameter 'serverName', defaultValue: 'cloudbees.devops.mousoft.co.kr', {
    expansionDeferred = '0'
    label = 'Server Name'
    orderIndex = '2'
    required = '1'
    type = 'entry'
  }

  formalParameter 'projName', defaultValue: 'University', {
    expansionDeferred = '0'
    label = 'Project Name'
    orderIndex = '3'
    required = '1'
    type = 'entry'
  }

  formalParameter 'artifactGroup', defaultValue: 'com.demo', {
    expansionDeferred = '0'
    label = 'Artifact Group Name'
    orderIndex = '4'
    required = '1'
    type = 'entry'
  }

  formalParameter 'apps', defaultValue: '''[
    [
        name: "University",
        artifactName: "university",
        tiers: ["app":"Spring","db":"MySql","web":"JBoss"]
    ],
]''', {
    description = ''
    expansionDeferred = '0'
    label = 'Application definitions'
    orderIndex = '5'
    required = '1'
    type = 'textarea'
  }

  formalParameter 'pipe', defaultValue: '''[
    name: "University Weekly Sprints",
    stages: ["QA", "Pre-prod", "Production","Audit Reports"]
]''', {
    expansionDeferred = '0'
    label = 'Release pipeline definition'
    orderIndex = '6'
    required = '1'
    type = 'textarea'
  }

  formalParameter 'major', defaultValue: '1', {
    expansionDeferred = '0'
    label = 'Major Version'
    orderIndex = '7'
    required = '1'
    type = 'entry'
  }

  formalParameter 'minor', defaultValue: '0', {
    expansionDeferred = '0'
    label = 'Minor Version'
    orderIndex = '8'
    required = '1'
    type = 'entry'
  }

  formalParameter 'patch', defaultValue: '1', {
    expansionDeferred = '0'
    label = 'Patch Version'
    orderIndex = '9'
    required = '1'
    type = 'entry'
  }

  formalParameter 'ec_stagesToRun', {
    expansionDeferred = '1'
    required = '0'
  }

  stage 'Setup', {
    description = ''
    colorCode = '#00adee'
    completionType = 'auto'
    pipelineName = 'University-CI'
    resourceName = 'DEV'
    waitForPlannedStartDate = '0'

    gate 'PRE', {
      }

    gate 'POST', {
      }

    task 'Check packages', {
      description = ''
      actualParameter = [
        'commandToRun': '''sudo git version
sudo mvn --version
sudo mysql -V''',
        'shellToUse': 'bash',
      ]
      advancedMode = '0'
      allowOutOfOrderRun = '0'
      alwaysRun = '0'
      enabled = '1'
      errorHandling = 'continueOnError'
      insertRollingDeployManualStep = '0'
      resourceName = ''
      skippable = '0'
      subpluginKey = 'EC-Core'
      subprocedure = 'RunCommand'
      taskType = 'COMMAND'
      useApproverAcl = '0'
      waitForPlannedStartDate = '0'
    }

    task 'JIRA - Issue status update [In Develop]', {
      description = ''
      actualParameter = [
        'issueKey': '$[/myPipelineRuntime/IssueKey]',
        'stepToApply': 'In Develope',
      ]
      advancedMode = '0'
      allowOutOfOrderRun = '0'
      alwaysRun = '0'
      enabled = '1'
      errorHandling = 'stopOnError'
      insertRollingDeployManualStep = '0'
      resourceName = ''
      skippable = '0'
      subprocedure = 'UpdateJIRAIssue'
      subproject = 'CO_WEB_DEMO'
      taskType = 'PROCEDURE'
      useApproverAcl = '0'
      waitForPlannedStartDate = '0'
    }
  }

  stage 'Build', {
    description = ''
    colorCode = '#ff7f0e'
    completionType = 'auto'
    pipelineName = 'University-CI'
    resourceName = 'DEV'
    waitForPlannedStartDate = '0'

    gate 'PRE', {
      }

    gate 'POST', {
      }

    task 'Clean Sources', {
      description = ''
      actualParameter = [
        'commandToRun': '''sudo rm -rf /DATA/workspace/mvnProject
sudo mkdir -p /DATA/workspace/mvnProject
sudo chmod 777 /DATA/workspace/mvnProject
sudo rm -rf /tmp/*.war''',
        'shellToUse': 'bash',
      ]
      advancedMode = '0'
      allowOutOfOrderRun = '0'
      alwaysRun = '0'
      enabled = '1'
      errorHandling = 'continueOnError'
      insertRollingDeployManualStep = '0'
      resourceName = ''
      skippable = '0'
      subpluginKey = 'EC-Core'
      subprocedure = 'RunCommand'
      taskType = 'COMMAND'
      useApproverAcl = '0'
      waitForPlannedStartDate = '0'
    }

    task 'Source Download', {
      description = ''
      actualParameter = [
        'clone': '0',
        'commit': '',
        'config': 'gitcfg',
        'depth': '',
        'dest': '/DATA/workspace/mvnProject',
        'GitBranch': 'master',
        'GitRepo': 'https://github.com/kin3303/efwebsample.git',
        'overwrite': '0',
        'resultPropertySheet': '/myPipelineRuntime/checkoutCode',
        'tag': '',
      ]
      advancedMode = '0'
      allowOutOfOrderRun = '0'
      alwaysRun = '0'
      enabled = '1'
      errorHandling = 'continueOnError'
      insertRollingDeployManualStep = '0'
      resourceName = ''
      skippable = '0'
      subpluginKey = 'ECSCM-Git'
      subprocedure = 'CheckoutCode'
      taskType = 'PLUGIN'
      useApproverAcl = '0'
      waitForPlannedStartDate = '0'
    }

    task 'Maven Build', {
      description = ''
      actualParameter = [
        'additionalOptions': '',
        'cmdDebug': '0',
        'cmdErrors': '0',
        'cmdFailatEnd': '0',
        'cmdFailFast': '0',
        'cmdFailNever': '0',
        'envVariables': '',
        'mavenCommand': 'package',
        'workingdirectory': '/DATA/workspace/mvnProject/univers',
      ]
      advancedMode = '0'
      allowOutOfOrderRun = '0'
      alwaysRun = '0'
      enabled = '1'
      errorHandling = 'continueOnError'
      insertRollingDeployManualStep = '0'
      resourceName = ''
      skippable = '0'
      subpluginKey = 'EC-Maven'
      subprocedure = 'runMaven'
      taskType = 'PLUGIN'
      useApproverAcl = '0'
      waitForPlannedStartDate = '0'
    }

    task 'JIRA - Issue status update', {
      description = ''
      advancedMode = '0'
      allowOutOfOrderRun = '0'
      alwaysRun = '0'
      enabled = '1'
      errorHandling = 'stopOnError'
      groupRunType = 'parallel'
      insertRollingDeployManualStep = '0'
      resourceName = ''
      skippable = '0'
      subproject = 'CO_WEB_DEMO'
      taskType = 'GROUP'
      useApproverAcl = '0'
      waitForPlannedStartDate = '0'

      task '[To Test]', {
        description = ''
        actualParameter = [
          'issueKey': '$[/myPipelineRuntime/IssueKey]',
          'stepToApply': 'Build Success',
        ]
        advancedMode = '0'
        allowOutOfOrderRun = '0'
        alwaysRun = '0'
        condition = '$[/javascript (\'$[/myStageRuntime/outcome]\' != \'error\')]'
        enabled = '1'
        errorHandling = 'stopOnError'
        groupName = 'JIRA - Issue status update'
        insertRollingDeployManualStep = '0'
        resourceName = ''
        skippable = '0'
        subprocedure = 'UpdateJIRAIssue'
        subproject = 'CO_WEB_DEMO'
        taskType = 'PROCEDURE'
        useApproverAcl = '0'
        waitForPlannedStartDate = '0'
      }

      task '[To Build Fail]', {
        description = ''
        actualParameter = [
          'issueKey': '$[/myPipelineRuntime/IssueKey]',
          'stepToApply': 'Build Fail',
        ]
        advancedMode = '0'
        allowOutOfOrderRun = '0'
        alwaysRun = '0'
        condition = '$[/javascript (\'$[/myStageRuntime/outcome]\' == \'error\')]'
        enabled = '1'
        errorHandling = 'stopOnError'
        groupName = 'JIRA - Issue status update'
        insertRollingDeployManualStep = '0'
        resourceName = ''
        skippable = '0'
        subprocedure = 'UpdateJIRAIssue'
        subproject = 'CO_WEB_DEMO'
        taskType = 'PROCEDURE'
        useApproverAcl = '0'
        waitForPlannedStartDate = '0'
      }
    }
  }

  stage 'Test', {
    description = ''
    colorCode = '#2ca02c'
    completionType = 'auto'
    pipelineName = 'University-CI'
    resourceName = 'DEV'
    waitForPlannedStartDate = '0'

    gate 'PRE', {
      }

    gate 'POST', {
      }

    task 'Code Style Check', {
      description = ''
      actualParameter = [
        'checkstylecommands': '',
        'commandtoexec': '/DATA/workspace/mvnProject/univers/checkStyle/checkstyle-5.4-all.jar',
        'configfile': '/DATA/workspace/mvnProject/univers/checkStyle/google_checks.xml',
        'javapath': '',
        'outputfile': '',
        'outputformat': 'plain',
        'propertiesfile': '',
        'targets': '/DATA/workspace/mvnProject/univers/univers-model/src/main/java/com/demo/model/entity/University.java',
        'targettype': 'file',
        'workingDir': '',
      ]
      advancedMode = '0'
      allowOutOfOrderRun = '0'
      alwaysRun = '0'
      enabled = '1'
      errorHandling = 'stopOnError'
      insertRollingDeployManualStep = '0'
      resourceName = ''
      skippable = '0'
      subpluginKey = 'EC-CheckStyle'
      subprocedure = 'runCheckStyle'
      taskType = 'PLUGIN'
      useApproverAcl = '0'
      waitForPlannedStartDate = '0'
    }

    task 'Code Static Analysis', {
      description = ''
      actualParameter = [
        'config': 'sonarcfg',
        'customValues': 'sonar.java.binaries="./target/classes" ',
        'resultFormat': 'propertysheet',
        'resultSonarProperty': '/myPipelineRuntime/runSonarScanner',
        'scannerDebug': '0',
        'scannerHeapSpace': '512',
        'sonarMetricsComplexity': 'all',
        'sonarMetricsDocumentation': 'all',
        'sonarMetricsDuplications': 'all',
        'sonarMetricsIssues': 'all',
        'sonarMetricsMaintainability': 'all',
        'sonarMetricsMetrics': 'all',
        'sonarMetricsQualityGates': 'all',
        'sonarMetricsReliability': 'all',
        'sonarMetricsSecurity': 'all',
        'sonarMetricsTests': 'all',
        'sonarProjectKey': 'SpringCodeTest',
        'sonarProjectName': 'SpringCodeTest',
        'sonarProjectVersion': '1',
        'sonarTimeout': '',
        'sourceEncoding': 'UTF-8',
        'sources': './src/main/java',
        'workDirectory': '/DATA/workspace/mvnProject/univers/univers-model',
      ]
      advancedMode = '0'
      allowOutOfOrderRun = '0'
      alwaysRun = '0'
      enabled = '1'
      errorHandling = 'stopOnError'
      insertRollingDeployManualStep = '0'
      resourceName = ''
      skippable = '0'
      subpluginKey = 'EC-SonarQube'
      subprocedure = 'Run Sonar Scanner'
      taskType = 'PLUGIN'
      useApproverAcl = '0'
      waitForPlannedStartDate = '0'
    }

    task 'JIRA - Issue status update', {
      description = ''
      advancedMode = '0'
      allowOutOfOrderRun = '0'
      alwaysRun = '0'
      enabled = '1'
      errorHandling = 'stopOnError'
      groupRunType = 'parallel'
      insertRollingDeployManualStep = '0'
      resourceName = ''
      skippable = '0'
      subproject = 'CO_WEB_DEMO'
      taskType = 'GROUP'
      useApproverAcl = '0'
      waitForPlannedStartDate = '0'

      task '[To Deploy]', {
        description = ''
        actualParameter = [
          'issueKey': '$[/myPipelineRuntime/IssueKey]',
          'stepToApply': 'Deploy',
        ]
        advancedMode = '0'
        allowOutOfOrderRun = '0'
        alwaysRun = '0'
        condition = '$[/javascript (\'$[/myStageRuntime/outcome]\' != \'error\')]'
        enabled = '1'
        errorHandling = 'stopOnError'
        groupName = 'JIRA - Issue status update'
        insertRollingDeployManualStep = '0'
        resourceName = ''
        skippable = '0'
        subprocedure = 'UpdateJIRAIssue'
        subproject = 'CO_WEB_DEMO'
        taskType = 'PROCEDURE'
        useApproverAcl = '0'
        waitForPlannedStartDate = '0'
      }

      task '[To Test Fail]', {
        description = ''
        actualParameter = [
          'issueKey': '$[/myPipelineRuntime/IssueKey]',
          'stepToApply': 'Test Fail',
        ]
        advancedMode = '0'
        allowOutOfOrderRun = '0'
        alwaysRun = '0'
        condition = '$[/javascript (\'$[/myStageRuntime/outcome]\' == \'error\')]'
        enabled = '1'
        errorHandling = 'stopOnError'
        groupName = 'JIRA - Issue status update'
        insertRollingDeployManualStep = '0'
        resourceName = ''
        skippable = '0'
        subprocedure = 'UpdateJIRAIssue'
        subproject = 'CO_WEB_DEMO'
        taskType = 'PROCEDURE'
        useApproverAcl = '0'
        waitForPlannedStartDate = '0'
      }
    }
  }

  stage 'Package', {
    description = ''
    colorCode = '#d62728'
    completionType = 'auto'
    pipelineName = 'University-CI'
    resourceName = 'DEV'
    waitForPlannedStartDate = '0'

    gate 'PRE', {
      }

    gate 'POST', {
      }

    task 'Publish DB', {
      description = ''
      actualParameter = [
        'artifactName': 'com.demo.university:db',
        'artifactVersionVersion': '$[major].$[minor].$[patch]-$[/increment /server/ec_counters/artifactCounter]',
        'compress': '1',
        'dependentArtifactVersionList': '',
        'excludePatterns': '',
        'followSymlinks': '1',
        'fromLocation': '/DATA/workspace/mvnProject/univers/univers-web/src/main/webapp/DB/data',
        'includePatterns': '*.sql',
        'repositoryName': 'default',
      ]
      advancedMode = '0'
      allowOutOfOrderRun = '0'
      alwaysRun = '0'
      enabled = '1'
      errorHandling = 'stopOnError'
      insertRollingDeployManualStep = '0'
      resourceName = ''
      skippable = '0'
      subpluginKey = 'EC-Artifact'
      subprocedure = 'Publish'
      taskType = 'PLUGIN'
      useApproverAcl = '0'
      waitForPlannedStartDate = '0'
    }

    task 'Publish App', {
      description = ''
      actualParameter = [
        'artifactName': 'com.demo.university:app',
        'artifactVersionVersion': '$[major].$[minor].$[patch]-$[/increment /server/ec_counters/artifactCounter]',
        'compress': '1',
        'dependentArtifactVersionList': '',
        'excludePatterns': '',
        'followSymlinks': '1',
        'fromLocation': '/DATA/workspace/mvnProject/univers/univers-web/target',
        'includePatterns': '*.war',
        'repositoryName': 'default',
      ]
      advancedMode = '0'
      allowOutOfOrderRun = '0'
      alwaysRun = '0'
      enabled = '1'
      errorHandling = 'stopOnError'
      insertRollingDeployManualStep = '0'
      resourceName = ''
      skippable = '0'
      subpluginKey = 'EC-Artifact'
      subprocedure = 'Publish'
      taskType = 'PLUGIN'
      useApproverAcl = '0'
      waitForPlannedStartDate = '0'
    }

    task 'Publish Web', {
      description = ''
      actualParameter = [
        'artifactName': 'com.demo.university:web',
        'artifactVersionVersion': '$[major].$[minor].$[patch]-$[/increment /server/ec_counters/artifactCounter]',
        'compress': '1',
        'dependentArtifactVersionList': '',
        'excludePatterns': '',
        'followSymlinks': '1',
        'fromLocation': '/DATA/workspace/mvnProject/univers/univers-web/src/main/webapp/WEB/config',
        'includePatterns': 'standalone.xml',
        'repositoryName': 'default',
      ]
      advancedMode = '0'
      allowOutOfOrderRun = '0'
      alwaysRun = '0'
      enabled = '1'
      errorHandling = 'stopOnError'
      insertRollingDeployManualStep = '0'
      resourceName = ''
      skippable = '0'
      subpluginKey = 'EC-Artifact'
      subprocedure = 'Publish'
      taskType = 'PLUGIN'
      useApproverAcl = '0'
      waitForPlannedStartDate = '0'
    }
  }

  stage 'Generate CD Pipeline', {
    description = ''
    colorCode = '#9467bd'
    completionType = 'auto'
    pipelineName = 'University-CI'
    waitForPlannedStartDate = '0'

    gate 'PRE', {
      }

    gate 'POST', {
      }

    task 'Generate CD', {
      description = ''
      actualParameter = [
        'apps': '''
$[/myPipelineRuntime/apps]''',
        'artifactGroup': '$[/myPipelineRuntime/artifactGroup]',
        'IssueKey': '$[/myPipelineRuntime/IssueKey]',
        'pipe': '$[/myPipelineRuntime/pipe]',
        'projName': '$[/myPipelineRuntime/projName]',
        'serverName': '$[/myPipelineRuntime/serverName]',
      ]
      advancedMode = '0'
      allowOutOfOrderRun = '0'
      alwaysRun = '0'
      enabled = '1'
      errorHandling = 'stopOnError'
      insertRollingDeployManualStep = '0'
      resourceName = ''
      skippable = '0'
      subErrorHandling = 'continueOnError'
      subpipeline = 'University-Gen-CD'
      subproject = 'CO_WEB_DEMO'
      taskType = 'PIPELINE'
      triggerType = 'async'
      useApproverAcl = '0'
      waitForPlannedStartDate = '0'
    }
  }

  // Custom properties

  property 'ec_counters', {

    // Custom properties
    pipelineCounter = '29'
  }
}