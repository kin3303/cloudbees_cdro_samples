pipeline 'University-Gen-CD', {
  description = ''
  disableMultipleActiveRuns = '0'
  disableRestart = '0'
  enabled = '1'
  overrideWorkspace = '0'
  projectName = 'CO_WEB_DEMO'
  skipStageMode = 'ENABLED'

  formalParameter 'serverName', defaultValue: '104.198.189.202', {
    expansionDeferred = '0'
    label = 'Server Name'
    orderIndex = '1'
    required = '1'
    type = 'entry'
  }

  formalParameter 'IssueKey', defaultValue: 'DOP-117', {
    expansionDeferred = '0'
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

  formalParameter 'ec_stagesToRun', {
    expansionDeferred = '1'
    required = '0'
  }

  stage 'Clean', {
    description = ''
    colorCode = '#ff7f0e'
    completionType = 'auto'
    pipelineName = 'University-Gen-CD'
    resourceName = 'DEV'
    waitForPlannedStartDate = '0'

    gate 'PRE', {
      }

    gate 'POST', {
      }

    task 'Check Old Resources', {
      description = ''
      actualParameter = [
        'commandToRun': '''def projName = "$[projName]"
def pipeline  = $[pipe]
def applications = $[apps]
def environments  = []
def resources = []

pipeline.stages.each { env -> 
	environments.push(env)
	
	def isProd = env.toLowerCase().contains("production")
	def isPreProd = env.toLowerCase().contains("pre-prod")
	def isQa = env.toLowerCase().contains("qa")
	
	applications[0].tiers.each { appTier, envTier -> 
			// create and add resource to the Tier
			def resCount = isProd?3:1
			(1..resCount).each { resNum ->
				def resName = (String) "${env}_${projName}_${envTier}_${resNum}"
				if(isPreProd){
					resName = resName + "-PRE-PROD"
				} else if(isQa) {
					resName = resName + "-QA"
				} else {
					resName = resName + "-PROD"
				} 
				resources.push(resName)
			}
		} 
}
 
def clean = ""

clean += "ectool deletePipeline \\"$projName\\" \\"$pipeline.name\\"\\n"

applications.name.each {
	clean += "ectool deleteApplication \\"$projName\\" \\"$it\\"\\n"
}

environments.each {
	clean += "ectool deleteEnvironment \\"$projName\\" \\"$it\\"\\n"
}

resources.each {
	clean += "ectool deleteResource \\"$it\\"\\n"
}

//clean += "ectool deleteProject \\"$projName\\""

setProperty "/myProject/clean", value: clean''',
        'shellToUse': 'ectool evalDsl --dslFile {0}',
      ]
      advancedMode = '0'
      allowOutOfOrderRun = '0'
      alwaysRun = '0'
      enabled = '1'
      errorHandling = 'stopOnError'
      insertRollingDeployManualStep = '0'
      resourceName = ''
      skippable = '0'
      subpluginKey = 'EC-Core'
      subprocedure = 'RunCommand'
      taskType = 'COMMAND'
      useApproverAcl = '0'
      waitForPlannedStartDate = '0'
    }

    task 'Clean Resources', {
      description = ''
      actualParameter = [
        'commandToRun': '$[/myProject/clean]',
      ]
      advancedMode = '0'
      allowOutOfOrderRun = '0'
      alwaysRun = '0'
      enabled = '1'
      errorHandling = 'stopOnError'
      insertRollingDeployManualStep = '0'
      resourceName = ''
      skippable = '0'
      subpluginKey = 'EC-Core'
      subprocedure = 'RunCommand'
      taskType = 'COMMAND'
      useApproverAcl = '0'
      waitForPlannedStartDate = '0'
    }

    task 'Save Old Resources Information', {
      description = ''
      actualParameter = [
        'commandToRun': '''project "$[/myProject/projectName]",{
	property "clean", value: """\\
		$[/myProject/clean]
		ectool deleteProject "$[projName]"
	""".stripIndent()
}''',
        'shellToUse': 'ectool evalDsl --dslFile {0}',
      ]
      advancedMode = '0'
      allowOutOfOrderRun = '0'
      alwaysRun = '0'
      enabled = '1'
      errorHandling = 'stopOnError'
      insertRollingDeployManualStep = '0'
      resourceName = ''
      skippable = '0'
      subpluginKey = 'EC-Core'
      subprocedure = 'RunCommand'
      taskType = 'COMMAND'
      useApproverAcl = '0'
      waitForPlannedStartDate = '0'
    }
  }

  stage 'Setting', {
    description = ''
    colorCode = '#2ca02c'
    completionType = 'auto'
    pipelineName = 'University-Gen-CD'
    resourceName = 'DEV'
    waitForPlannedStartDate = '0'

    gate 'PRE', {
      }

    gate 'POST', {
      }

    task 'Set up permissions', {
      description = ''
      actualParameter = [
        'commandToRun': '''def projName = "$[projName]"

aclEntry projectName: "/plugins/EC-Artifact/project",
	objectType: "project",
	principalName: "project: $projName",
	principalType: "user",
	executePrivilege: "allow"

aclEntry projectName: "/plugins/EC-Core/project",
	objectType: "project",
	principalName: "project: $projName",
	principalType: "user",
	executePrivilege: "allow"''',
        'shellToUse': 'ectool evalDsl --dslFile {0}',
      ]
      advancedMode = '0'
      allowOutOfOrderRun = '0'
      alwaysRun = '0'
      enabled = '1'
      errorHandling = 'stopOnError'
      insertRollingDeployManualStep = '0'
      resourceName = ''
      skippable = '0'
      subpluginKey = 'EC-Core'
      subprocedure = 'RunCommand'
      taskType = 'COMMAND'
      useApproverAcl = '0'
      waitForPlannedStartDate = '0'
    }
  }

  stage 'Generate', {
    description = ''
    colorCode = '#9467bd'
    completionType = 'auto'
    pipelineName = 'University-Gen-CD'
    resourceName = 'DEV'
    waitForPlannedStartDate = '0'

    gate 'PRE', {
      }

    gate 'POST', {
      }

    task 'Generate procedures', {
      description = ''
      actualParameter = [
        'commandToRun': '''def projName = "$[projName]"
def subProjName = "$[/myProject/projectName]"

project projName, {
	procedure "AppValidation",{
		step "URL Test",
			command: "echo testing URL"
	}

	procedure "FunctionalTests",{
		step "Selenium Test",
			command: "echo selenium test"
	}

	procedure "SeleniumTests",{
		step "Selenium Test",
			command: "echo selenium test"
	}

	procedure "FunctionalTests",{
		step "Functional Test",
			command: "echo functions test"
	}
	
	procedure "IntegrationTests",{
		step "Integration Test",
			command: "echo integration test"
	}
	
	procedure "SmokeTests",{
		step "Integration Test",
			command: "echo smoke test"
	}
				
	procedure "UpdateTicket",{
		step "URL Test",
			command: "echo update ticket in jira"
	}
    
	procedure("Update Ticket For Deploy - $[issueKey]"){
		step(\'update jira ticket\') {
			subproject=subProjName
			subprocedure="UpdateJIRAIssue"
			actualParameter(\'issueKey\', "$[issueKey]")
			actualParameter(\'stepToApply\', \'Deploy\')
		}
	}
	
	procedure("Update Ticket For Rollback - $[issueKey]"){
		step(\'update jira ticket\') {
			subproject=subProjName
			subprocedure="UpdateJIRAIssue"
			actualParameter(\'issueKey\', "$[issueKey]")
			actualParameter(\'stepToApply\', \'Rollback\')
		}
	}
	
	procedure("Update Ticket For Finish - $[issueKey]"){
		step("update jira ticket - $[issueKey]") {
			subproject=subProjName
			subprocedure="UpdateJIRAIssue"
			actualParameter(\'issueKey\', "$[issueKey]")
			actualParameter(\'stepToApply\', \'Done\')
		}
	}

	procedure "GenerateQAResult", { 
		step "Collect Test Results",
			command: "ectool setProperty /myPipelineStageRuntime/ec_summary/testResults " +
				// Dummy location...
				"\\\'" + \'<html><a href=\\"http://$[serverName]:8060/univers-web/ui\\">Test Results</a></html>\' + "\\\'"
	}
	
	procedure "GeneratePreProdResult", { 
		step "Collect Test Results",
			command: "ectool setProperty /myPipelineStageRuntime/ec_summary/testResults " +
				// Dummy location...
				"\\\'" + \'<html><a href=\\"http://$[serverName]:8070/univers-web/ui\\">Test Results</a></html>\' + "\\\'"
	}
	
	procedure "GenerateProdResult", { 
		step "Collect Test Results",
			command: "ectool setProperty /myPipelineStageRuntime/ec_summary/testResults " +
				// Dummy location...
				"\\\'" + \'<html><a href=\\"http://$[serverName]:8080/univers-web/ui\\">Test Results</a></html>\' + "\\\'"
	}
}
 ''',
        'shellToUse': 'ectool evalDsl --dslFile {0}',
      ]
      advancedMode = '0'
      allowOutOfOrderRun = '0'
      alwaysRun = '0'
      enabled = '1'
      errorHandling = 'stopOnError'
      insertRollingDeployManualStep = '0'
      resourceName = ''
      skippable = '0'
      subpluginKey = 'EC-Core'
      subprocedure = 'RunCommand'
      taskType = 'COMMAND'
      useApproverAcl = '0'
      waitForPlannedStartDate = '0'
    }

    task 'Generate environments', {
      description = ''
      actualParameter = [
        'commandToRun': '''def pipe  = $[pipe]
def projName = "$[projName]"
def apps = $[apps]

def envs = pipe.stages

def resources = []

project projName, {
	pipe.stages.each { env ->
		environment env, {
			// Only in Prod
			def isProd = env.toLowerCase().contains("production")
			def isPreProd = env.toLowerCase().contains("pre-prod")
			def isQa = env.toLowerCase().contains("qa")
			
			if (isProd) {				
				rollingDeployEnabled = \'1\'
				rollingDeployType = \'phase\'

				["Green","Blue"].each { phase ->
					rollingDeployPhase phase, {
						orderIndex = \'1\'
						phaseExpression = null
						rollingDeployPhaseType = \'tagged\'
					}
				}
			}

			apps[0].tiers.each { appTier, envTier ->
		
				environmentTier envTier, {
					// create and add resource to the Tier
					def resCount = isProd?2:1
					(1..resCount).each { resNum ->
						def resName = (String) "${env}_${projName}_${envTier}_${resNum}"
						if(isPreProd){
							resName = resName + "-PRE-PROD"
							resource resourceName: resName, hostName : "devagent"
						} else if(isQa) {
							resName = resName + "-QA"
							resource resourceName: resName, hostName : "qaagent"
						} else {
							resName = resName + "-PROD"
							resource resourceName: resName, hostName : "prodagent"
						} 
						resources.push(resName)
						
						def phase = (resNum==1)?\'Blue\':\'Green\'
						if (isProd) {
							environmentTier envTier, resourcePhaseMapping: [(resName) : phase]
						}
					} // each resource
				} // environmentTier
			} // each app
		} // environment
	} // Environments
} // Project projName

def clean = getProperty("/myProject/clean").value
resources.each {
	clean += "ectool deleteResource \\"$it\\"\\n"
}
setProperty "/myProject/clean", value: clean''',
        'shellToUse': 'ectool evalDsl --dslFile {0}',
      ]
      advancedMode = '0'
      allowOutOfOrderRun = '0'
      alwaysRun = '0'
      enabled = '1'
      errorHandling = 'stopOnError'
      insertRollingDeployManualStep = '0'
      resourceName = ''
      skippable = '0'
      subpluginKey = 'EC-Core'
      subprocedure = 'RunCommand'
      taskType = 'COMMAND'
      useApproverAcl = '0'
      waitForPlannedStartDate = '0'
    }

    task 'Generate applications', {
      description = ''
      actualParameter = [
        'commandToRun': '''def apps = $[apps]
def projName = "$[projName]"
def artifactGroup = "$[artifactGroup]"
def pipe  = $[pipe]

def envs = pipe.stages

apps.each { app -> 
	project projName, { 
		def appName = app.name
		application applicationName: appName, {
			app.tiers.each { appTier, envTier ->
				applicationTier appTier, {
					component appTier, pluginKey: \'EC-Artifact\', {
						ec_content_details.with { 
							pluginprojName = \'EC-Artifact\'
							pluginProcedure = \'Retrieve\'
							artifactName = "${artifactGroup}.${app.artifactName}:${appTier}"
							filterList = \'\'
							overwrite = \'update\'
							versionRange = \'\'
							artifactVersionLocationProperty = \'/myJob/retrievedArtifactVersions/\'+\'$\'+\'[assignedResourceName]\'
						}

						process processName: "Deploy", processType: "DEPLOY", componentApplicationName: appName, {
							processStep "Retrieve Artifact", {
								processStepType = "component"
								subprocedure = "Retrieve"
								errorHandling = "failProcedure"
								subproject = "/plugins/EC-Artifact/project"
								actualParameter = [ 
									artifactName : \'$\'+\'[/myComponent/ec_content_details/artifactName]\',
									artifactVersionLocationProperty : \'$\'+\'[/myComponent/ec_content_details/artifactVersionLocationProperty]\',
									filterList : \'$\'+\'[/myComponent/ec_content_details/filterList]\',
									overwrite : \'$\'+\'[/myComponent/ec_content_details/overwrite]\',
									versionRange : \'$\'+\'[ec_\'+app.artifactName+\'-version]\'
								]
							}
							def isApp = appTier.toLowerCase().contains("app")
							def isdb = appTier.toLowerCase().contains("db")
							def isweb = appTier.toLowerCase().contains("web")
							if(isApp){
								processStep "Deploy Artifact", {
									errorHandling = \'failProcedure\'
									processStepType = \'command\'
									subproject = \'/plugins/EC-Core/project\'
									subprocedure = \'RunCommand\'
									actualParameter = [
										commandToRun: \'\'\'sudo rm -rf /tmp/*.war
										sudo cp ./univers-web-0.0.1-SNAPSHOT.war /tmp/univers-web.war
										sudo /opt/jboss/wildfly/bin/jboss-cli.sh --connect --command=\\"deploy --force /tmp/univers-web.war\\"
										sudo /opt/jboss/wildfly/bin/jboss-cli.sh --connect --command=deployment-info
										\'\'\'
										]
									workingDirectory = \'$\'+\'[/myJob/retrievedArtifactVersions/\'+\'$\'+\'[assignedResourceName]/\'+\'$\'+\'[/myComponent/ec_content_details/artifactName]/cacheLocation]\'
								}
							}else if(isdb){
								processStep "Deploy Artifact", {
									errorHandling = \'failProcedure\'
									processStepType = \'command\'
									subproject = \'/plugins/EC-Core/project\'
									subprocedure = \'RunCommand\'
									actualParameter = [
										commandToRun: \'\'\'sudo rm -rf /tmp/*.sql
										sudo cp ./test.sql /tmp/test.sql
										sudo mysql -u root -ppassword < /tmp/test.sql 
										\'\'\'
										]
									workingDirectory = \'$\'+\'[/myJob/retrievedArtifactVersions/\'+\'$\'+\'[assignedResourceName]/\'+\'$\'+\'[/myComponent/ec_content_details/artifactName]/cacheLocation]\'
								}
							}else{
								processStep "Deploy Artifact", {
									errorHandling = \'failProcedure\'
									processStepType = \'command\'
									subproject = \'/plugins/EC-Core/project\'
									subprocedure = \'RunCommand\'
									actualParameter = [
										commandToRun: \'\'\'echo \\"JBOSS Re-Configuration\\"
										#sudo mv /tmp/standardalone.xml /opt/jboss/wildfly/conf/standalone.xml
										\'\'\'
										]
								}
							}
							createProcessDependency componentApplicationName: appName,
								processStepName: "Retrieve Artifact",
								targetProcessStepName: "Deploy Artifact"	
						}
						
						process \'UnDeploy\', {
							processType = \'UNDEPLOY\'
							componentApplicationName = appName
							description = \'Linear undeploy process\'
		
							def isApp = appTier.toLowerCase().contains(\'app\')
							def isdb = appTier.toLowerCase().contains(\'db\')
							def isweb = appTier.toLowerCase().contains(\'web\')
							if(isApp){
								processStep \'Remove Artifact\', {
									errorHandling = \'failProcedure\'
									processStepType = \'command\'
									subproject = \'/plugins/EC-Core/project\'
									subprocedure = \'RunCommand\'
									workingDirectory = \'$\'+\'[/myJob/retrievedArtifactVersions/\'+\'$\'+\'[assignedResourceName]/\'+\'$\'+\'[/myComponent/ec_content_details/artifactName]/cacheLocation]\'
									actualParameter = [
										commandToRun: \'\'\'sudo rm -rf /tmp/*.war
										sudo /opt/jboss/wildfly/bin/jboss-cli.sh --connect --command=\\\'undeploy univers-web.war\\\'
										\'\'\'
									]
								}
							}else if(isdb){
								processStep \'Remove Artifact\', {
									errorHandling = \'failProcedure\'
									processStepType = \'command\'
									subproject = \'/plugins/EC-Core/project\'
									subprocedure = \'RunCommand\'
									workingDirectory = \'$\'+\'[/myJob/retrievedArtifactVersions/$\'+\'[assignedResourceName]/\'+\'$\'+\'[/myComponent/ec_content_details/artifactName]/cacheLocation]\'
									actualParameter = [
										commandToRun: \'\'\'sudo rm -rf /tmp/*.sql
										\'\'\'
									]
								}
							}else{
								processStep \'Remove Artifact\', {
									errorHandling = \'failProcedure\'
									processStepType = \'command\'
									subproject = \'/plugins/EC-Core/project\'
									subprocedure = \'RunCommand\'
									actualParameter = [
										commandToRun: \'\'\'sudo rm -rf /tmp/*.xml
										 \'\'\'
									]
								}										
							}
						}	
					}
				}
			}
			
			process "Deploy",{
				formalParameter "changeType"
				
				processStep \'Validation\', {
					applicationTierName = \'DB\'
					errorHandling = \'failProcedure\'
					processStepType = \'procedure\'
					subprocedure = \'AppValidation\'
					subproject = projName
				}
				processStep \'Rollback\', {
					processStepType = \'rollback\'
					rollbackType = \'environment\'
					smartRollback = \'0\'
					dependencyJoinType = \'or\'
				}
				processDependency \'Validation\', targetProcessStepName: \'Rollback\', branchType: \'ALWAYS\',
					branchCondition: \'$\'+\'[/javascript myJob.outcome==\\\'error\\\']\',
					branchConditionName: \'On error\',
					branchConditionType: \'CUSTOM\'
				app.tiers.each { appTier, envTier ->
					processStep  appTier, {
						processStepType = \'process\'
						componentName = null
						applicationName = appName
						componentApplicationName = appName
						errorHandling = \'failProcedure\'
						subcomponent = appTier
						subcomponentApplicationName = appName
						subcomponentProcess = "Deploy"
						applicationTierName = appTier
					}
					processDependency appTier, targetProcessStepName: \'Validation\', branchType: \'ALWAYS\'
				}
			}

			process "DeployForQA",{
				formalParameter "changeType"
				
				processStep \'Validation\', {
					applicationTierName = \'DB\'
					errorHandling = \'failProcedure\'
					processStepType = \'procedure\'
					subprocedure = \'AppValidation\'
					subproject = projName
				}
				processStep \'Rollback\', {
					processStepType = \'rollback\'
					rollbackType = \'environment\'
					smartRollback = \'0\'
					dependencyJoinType = \'or\'
				}
				processDependency \'Validation\', targetProcessStepName: "Rollback", branchType: \'ALWAYS\', 
					branchCondition: \'$\'+\'[/javascript myJob.outcome==\\\'error\\\']\',
					branchConditionName: \'On error\',
					branchConditionType: \'CUSTOM\'					
				app.tiers.each { appTier, envTier ->
					processStep  appTier, {
						processStepType = \'process\'
						componentName = null
						applicationName = appName
						componentApplicationName = appName
						errorHandling = \'failProcedure\'
						subcomponent = appTier
						subcomponentApplicationName = appName
						subcomponentProcess = "Deploy"
						applicationTierName = appTier
					}
					processDependency appTier, targetProcessStepName: \'Validation\', branchType: \'ALWAYS\'
				}
			}
			process \'UnDeploy\',{
				app.tiers.each { appTier, envTier ->
					processStep  appTier, {
						processStepType = \'process\' 
						applicationName = appName
						componentApplicationName = appName
						errorHandling = \'failProcedure\'
						subcomponent = appTier
						subcomponentApplicationName = appName
						subcomponentProcess = \'UnDeploy\'
						applicationTierName = appTier
					}	
				 }
			}
			envs.each { env ->
				app.tiers.each { appTier, envTier ->
					tierMap tierMapName: "${appName}-$env",
						environmentProjectName: projectName,
						environmentName: env,
						tierMapping: [(appTier):envTier]
				}
			}
		}
	}
}''',
        'shellToUse': 'ectool evalDsl --dslFile {0}',
      ]
      advancedMode = '0'
      allowOutOfOrderRun = '0'
      alwaysRun = '0'
      enabled = '1'
      errorHandling = 'stopOnError'
      insertRollingDeployManualStep = '0'
      resourceName = ''
      skippable = '0'
      subpluginKey = 'EC-Core'
      subprocedure = 'RunCommand'
      taskType = 'COMMAND'
      useApproverAcl = '0'
      waitForPlannedStartDate = '0'
    }

    task 'Generate Release Pipeline', {
      description = ''
      actualParameter = [
        'commandToRun': '''def pipe  = $[pipe]
def projName = "$[projName]"

project projName, {

	property "normalReleaseMode" // Used to flag whether tasks are run or not; use 0 to run seeding release
	pipeline pipe.name+" - $[issueKey]", {
		pipe.stages.eachWithIndex { st, index ->
			stage st,{
				resourceName = "local"
				
				if (index == 0) {
					colorCode = \'#ff7f0e\'
				
					gate \'PRE\', {
					}

					gate \'POST\', { 
					  task \'Ensure 75% Passing Tests\', { 
						enabled = \'1\'
						errorHandling = \'stopOnError\'
						gateCondition =  \'$\'+\'[/javascript getProperty("/myStageRuntime/outcome")!="error"]\'
						gateType = \'POST\'
						insertRollingDeployManualStep = \'0\'
						resourceName = \'\'
						skippable = \'0\'
						subproject = \'University\'
						taskType = \'CONDITIONAL\'
						useApproverAcl = \'0\'
						waitForPlannedStartDate = \'0\'
					  }
					} 
	
					task \'Deploy App\', {
					  description = \'\'
					  actualParameter = [
						\'ec_enforceDependencies\': \'1\',
						\'ec_smartDeployOption\': \'1\',
						\'ec_stageArtifacts\': \'1\',
					  ]
					  advancedMode = \'0\'
					  allowOutOfOrderRun = \'0\'
					  alwaysRun = \'0\'
					  enabled = \'1\'
					  environmentName = \'QA\'
					  environmentProjectName = \'University\'
					  errorHandling = \'stopOnError\'
					  insertRollingDeployManualStep = \'0\'
					  resourceName = \'\'
					  rollingDeployEnabled = \'0\'
					  skippable = \'0\'
					  subapplication = \'University\'
					  subprocess = \'DeployForQA\'
					  subproject = \'University\'
					  taskProcessType = \'APPLICATION\'
					  taskType = \'PROCESS\'
					  useApproverAcl = \'0\'
					  waitForPlannedStartDate = \'0\'
					}
					
					task "Run functional Test",
						taskType: \'PROCEDURE\',
						subproject: projName,
						subprocedure: "FunctionalTests", 
                        errorHandling: "stopOnError"
					task "Generate QA Result",
						taskType: \'PROCEDURE\',
						subproject: projName,
						subprocedure: "GenerateQAResult", 
                        errorHandling: "stopOnError" 
				} 
					
				if (index == 1) {
					colorCode = \'#2ca02c\'
					
					task \'Deploy App\', {
					  description = \'\'
					  actualParameter = [
						\'ec_enforceDependencies\': \'1\',
						\'ec_smartDeployOption\': \'1\',
						\'ec_stageArtifacts\': \'1\',
					  ]
					  advancedMode = \'0\'
					  allowOutOfOrderRun = \'0\'
					  alwaysRun = \'0\'
					  enabled = \'1\'
					  environmentName = \'Pre-prod\'
					  environmentProjectName = \'University\'
					  errorHandling = \'stopOnError\'
					  insertRollingDeployManualStep = \'0\'
					  resourceName = \'\'
					  rollingDeployEnabled = \'0\'
					  skippable = \'0\'
					  subapplication = \'University\'
					  subprocess = \'Deploy\'
					  subproject = \'University\'
					  taskProcessType = \'APPLICATION\'
					  taskType = \'PROCESS\'
					  useApproverAcl = \'0\'
					  waitForPlannedStartDate = \'0\'
					}
	
					task "Run SIT Test",
						taskType: \'PROCEDURE\',
						subproject: projName,
						subprocedure: "IntegrationTests", 
                        errorHandling: "stopOnError"
					task "Generate Pre-Prod Result",
						taskType: \'PROCEDURE\',
						subproject: projName,
						subprocedure: "GeneratePreProdResult", 
                        errorHandling: "stopOnError"
		
					task \'Send Slack Notification\', {
					  description = \'\'
					  actualParameter = [
						\'config\': \'slackcfg\',
						\'payload-msg\': \'\'\'{"text": "Check pre-production result. 
<http://$[serverName]:8070/univers-web/ui|Click here> for details!"}\'\'\',
						\'resultFormat\': \'json\',
						\'resultPropertySheet\': \'/myJob/sendRTMStatus\',
					  ]
					  advancedMode = \'0\'
					  allowOutOfOrderRun = \'0\'
					  alwaysRun = \'0\'
					  enabled = \'1\'
					  errorHandling = \'stopOnError\'
					  insertRollingDeployManualStep = \'0\'
					  resourceName = \'\'
					  skippable = \'0\'
					  subpluginKey = \'EC-Slack\'
					  subprocedure = \'Send Realtime Message\'
					  taskType = \'PLUGIN\'
					  useApproverAcl = \'0\'
					  waitForPlannedStartDate = \'0\'
					}
					
					task \'Veryfy QA/Notify\', { 
						emailConfigName = \'gmail\' 
						errorHandling = \'stopOnError\' 
						instruction = \'Verify that business requirements are met\' 
						notificationTemplate = \'ec_default_pipeline_manual_task_notification_template\'
						taskType = \'MANUAL\' 
						approver = [
							\'development\',\'admin\'
						]
					}
					
					task "JIRA - Issue status update - [To Deploy]",
						taskType: \'PROCEDURE\',
						subproject: projName,
						subprocedure: "Update Ticket For Deploy - $[issueKey]"
				}
					
				if (index == 2) {
					colorCode = \'#00adee\'
					
					task \'Deploy App - Blue Phase\', {
					  description = \'\'
					  actualParameter = [
						\'ec_enforceDependencies\': \'1\',
						\'ec_smartDeployOption\': \'1\',
						\'ec_stageArtifacts\': \'1\',
					  ]
					  advancedMode = \'0\'
					  allowOutOfOrderRun = \'0\'
					  alwaysRun = \'0\'
					  enabled = \'1\'
					  environmentName = \'Production\'
					  environmentProjectName = \'University\'
					  errorHandling = \'stopOnError\'
					  insertRollingDeployManualStep = \'0\'
					  resourceName = \'\'
					  rollingDeployEnabled = \'1\'
					  skippable = \'0\'
					  subapplication = \'University\'
					  subprocess = \'Deploy\'
					  subproject = \'University\'
					  taskProcessType = \'APPLICATION\'
					  taskType = \'PROCESS\'
					  useApproverAcl = \'0\'
					  waitForPlannedStartDate = \'0\'
					  rollingDeployPhase = [
						\'Blue\',
					  ]
					}
				
					task "Run Smoke Test",
						taskType: \'PROCEDURE\',
						subproject: projName,
						subprocedure: "SmokeTests", 
                        errorHandling: "stopOnError"

					task \'Deploy App - Green Phase\', {
					  description = \'\'
					  actualParameter = [
						\'ec_enforceDependencies\': \'1\',
						\'ec_smartDeployOption\': \'1\',
						\'ec_stageArtifacts\': \'1\',
					  ]
					  advancedMode = \'0\'
					  allowOutOfOrderRun = \'0\'
					  alwaysRun = \'0\'
					  condition = \'$\'+\'[/javascript getProperty("/myStageRuntime/outcome")!="error"]\'
					  enabled = \'1\'
					  environmentName = \'Production\'
					  environmentProjectName = \'University\'
					  errorHandling = \'stopOnError\'
					  insertRollingDeployManualStep = \'0\'
					  resourceName = \'\'
					  rollingDeployEnabled = \'1\'
					  skippable = \'0\'
					  subapplication = \'University\'
					  subprocess = \'Deploy\'
					  subproject = \'University\'
					  taskProcessType = \'APPLICATION\'
					  taskType = \'PROCESS\'
					  useApproverAcl = \'0\'
					  waitForPlannedStartDate = \'0\'
					  rollingDeployPhase = [
						\'Green\',
					  ]
					}
						
					task "Generate Production Result",
						taskType: \'PROCEDURE\',
						subproject: projName,
						subprocedure: "GenerateProdResult", 
                        errorHandling: "stopOnError"
					task  "JIRA - Issue status update - [To Finish]",
						taskType: \'PROCEDURE\',
						subproject: projName,
						subprocedure: "Update Ticket For Finish - $[issueKey]" 
					task "Release Management",  // Don\'t create a gate for first stage
						taskType: \'APPROVAL\',
						approver: [\'admin\'],
						gateType: \'PRE\',
						notificationTemplate: \'ec_default_pipeline_notification_template\'
				}

				if (index == 3) { 
					colorCode = \'#d62728\' 

					task \'Generate Approval Audit Report\', { 
					  advancedMode = \'0\'
					  allowOutOfOrderRun = \'0\'
					  alwaysRun = \'0\'
					  enabled = \'1\'
					  errorHandling = \'stopOnError\'
					  insertRollingDeployManualStep = \'0\'
					  resourceName = \'\'
					  skippable = \'0\'
					  subpluginKey = \'EC-AuditReports\'
					  subprocedure = \'generateApprovalAuditReport\'
					  taskType = \'PLUGIN\'
					  useApproverAcl = \'0\'
					  waitForPlannedStartDate = \'0\'
					}

					task \'Generate Evidence Links Audit Report\', {
					  description = \'\'
					  advancedMode = \'0\'
					  allowOutOfOrderRun = \'0\'
					  alwaysRun = \'0\'
					  enabled = \'1\'
					  errorHandling = \'stopOnError\'
					  insertRollingDeployManualStep = \'0\'
					  resourceName = \'\'
					  skippable = \'0\'
					  subpluginKey = \'EC-AuditReports\'
					  subprocedure = \'generateEvidenceLinksAuditReport\'
					  taskType = \'PLUGIN\'
					  useApproverAcl = \'0\'
					  waitForPlannedStartDate = \'0\'
					}

					task \'Generate Task Duration Audit Report\', {
					  description = \'\'
					  advancedMode = \'0\'
					  allowOutOfOrderRun = \'0\'
					  alwaysRun = \'0\'
					  enabled = \'1\'
					  errorHandling = \'stopOnError\'
					  insertRollingDeployManualStep = \'0\'
					  resourceName = \'\'
					  skippable = \'0\'
					  subpluginKey = \'EC-AuditReports\'
					  subprocedure = \'generateTaskDurationAuditReport\'
					  taskType = \'PLUGIN\'
					  useApproverAcl = \'0\'
					  waitForPlannedStartDate = \'0\'
					}				
				}
			} // stages
		} // Each stage
	} // Pipeline
} // Project''',
        'shellToUse': 'ectool evalDsl --dslFile {0}',
      ]
      advancedMode = '0'
      allowOutOfOrderRun = '0'
      alwaysRun = '0'
      enabled = '1'
      errorHandling = 'stopOnError'
      insertRollingDeployManualStep = '0'
      resourceName = ''
      skippable = '0'
      subpluginKey = 'EC-Core'
      subprocedure = 'RunCommand'
      taskType = 'COMMAND'
      useApproverAcl = '0'
      waitForPlannedStartDate = '0'
    }
  }

  // Custom properties

  property 'ec_counters', {

    // Custom properties
    pipelineCounter = '63'
  }
}