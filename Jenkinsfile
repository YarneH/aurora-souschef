#!/usr/bin/env groovy
import net.sf.json.JSONArray
import net.sf.json.JSONObject

pipeline {
    agent any

    environment {
        scannerHome = tool 'sonarscanner'
    }

    stages {
        stage('Compile') {

            steps {
                // Compile the app and its dependencies
                  sh './gradlew clean compileDebugSources'
            }

            post {
                failure {
                    slack_error_build()
                }
            }
        } // Compile stage

        // Short unit tests
        stage('Unit test') {
            steps {
                script {
                    // Compile and run the unit tests for the app and its dependencies
                    if (env.BRANCH_NAME == 'master' || env.BRANCH_NAME == 'dev') {
                        sh "./gradlew testReleaseUnitTest --tests '*UnitTest'"
                        sh "./gradlew souschefprocessor:testReleaseUnitTest --tests '*UnitTest'"
                    } else {
                        sh "./gradlew testDebugUnitTest --tests '*UnitTest'"
                        sh "./gradlew souschefprocessor:testDebugUnitTest --tests '*UnitTest'"
                    }


                    // Analyse the test results and update the build result as appropriate
                    junit allowEmptyResults: true, testResults: '**/TEST-*.xml'

                    // Analyze coverage info
                    jacoco sourcePattern: '**/src/main/java/com/aurora', 
                        classPattern: '**/classes/com/aurora', 
                        exclusionPattern: '**/*Test*.class,  **/souschef/*.class, **/R.class, **/R$*.class, **/BuildConfig'
                }
            }

            post {
                failure {
                    slack_error_test()
                }
            }
        } // Unit test stage

        stage('SonarQube') {
            steps {
                script {
                    if (env.BRANCH_NAME == 'master' || env.BRANCH_NAME == 'dev') {
                        withSonarQubeEnv("Aurora SonarQube") {
                        sh """
                        ${scannerHome}/bin/sonar-scanner -X -Dproject.settings=sonar-project.properties -Dsonar.branch=${env.BRANCH_NAME} \
                        -Dapp.sonar.java.binaries=build/intermediates/javac/release/compileReleaseJavaWithJavac \
                        -Dsouschefprocessor.sonar.java.binaries=build/intermediates/javac/release/compileReleaseJavaWithJavac
                        """
                        }
                    } else {
                        withSonarQubeEnv("Aurora SonarQube") {
                        sh """
                        ${scannerHome}/bin/sonar-scanner -X -Dproject.settings=sonar-project.properties -Dsonar.branch=${env.BRANCH_NAME} \
                        -Dapp.sonar.java.binaries=build/intermediates/javac/debug/compileDebugJavaWithJavac \
                        -Dsouschefprocessor.sonar.java.binaries=build/intermediates/javac/debug/compileDebugJavaWithJavac
                        """
                        }
                    }

                    timeout(time: 1, unit: 'HOURS') {
                        def qg = waitForQualityGate()
                        if (qg.status != 'OK') {
                            error 'Sonarqube failed.'
                        }
                    }
                }
            }

            post {
                failure {
                    slack_error_sonar()
                }
                success {
                    slack_success_part1()
                }
            }
        } // SonarQube stage

        stage('Long Unit Tests') {
            steps {
                script {
                    // Compile and run the unit tests for the app and its dependencies
                    if (env.BRANCH_NAME == 'master' || env.BRANCH_NAME == 'dev') {
                        sh "./gradlew testReleaseUnitTest --tests '*LongTest'"
                        sh "./gradlew souschefprocessor:testReleaseUnitTest --tests '*LongTest'"
                    } else {
                        sh "./gradlew testDebugUnitTest --tests '*LongTest'"
                        sh "./gradlew souschefprocessor:testDebugUnitTest --tests '*LongTest'"
                    }


                    // Analyse the test results and update the build result as appropriate
                    junit allowEmptyResults: true, testResults: '**/TEST-*.xml'

                    // Analyze coverage info
                    jacoco sourcePattern: '**/src/main/java/com/aurora', 
                        classPattern: '**/classes/com/aurora', 
                        exclusionPattern: '**/*Test*.class,  **/souschef/*.class, **/R.class, **/R$*.class, **/BuildConfig'
                }
            }
            post {
                failure {
                    slack_error_long_test()
                }
            }
        } // Long Unit tests stage

        stage('Integration Tests') {
            steps {
                script {
                    // Compile and run the unit tests for the app and its dependencies
                    if (env.BRANCH_NAME == 'master' || env.BRANCH_NAME == 'dev') {
                        sh "./gradlew testReleaseUnitTest --tests '*IntegTest'"
                        sh "./gradlew souschefprocessor:testReleaseUnitTest --tests '*IntegTest'"
                    } else {
                        sh "./gradlew testDebugUnitTest --tests '*IntegTest'"
                        sh "./gradlew souschefprocessor:testDebugUnitTest --tests '*IntegTest'"
                    }


                    // Analyse the test results and update the build result as appropriate
                    junit allowEmptyResults: true, testResults: '**/TEST-*.xml'

                    // Analyze coverage info
                    jacoco sourcePattern: '**/src/main/java/com/aurora', 
                        classPattern: '**/classes/com/aurora', 
                        exclusionPattern: '**/*Test*.class,  **/souschef/*.class, **/R.class, **/R$*.class, **/BuildConfig'
                }
            }
            post {
                failure {
                    slack_error_integration_test()
                }
            }
        }

        stage('Javadoc') {
            when {
                anyOf {
                    branch 'master';
                    branch 'dev';
                }
            }
            steps {
                // Generate javadoc
                sh """
                javadoc -d /var/www/javadoc/souschef/app/${env.BRANCH_NAME} -sourcepath ${WORKSPACE}/app/src/main/java -subpackages com -private \
                -classpath ${WORKSPACE}/app/build/intermediates/javac/release/compileReleaseJavaWithJavac/classes
                """

                sh """
                javadoc -d /var/www/javadoc/souschef/souschefprocessor/${env.BRANCH_NAME} -sourcepath ${WORKSPACE}/souschefprocessor/src/main/java -subpackages com -private \
                -classpath ${WORKSPACE}/app/build/intermediates/javac/release/compileReleaseJavaWithJavac/classes
                """
            }
            post {
                failure {
                    slack_error_doc()
                }
            }
        } // Javadoc stage
    } // Stages

    post {
        success {
            slack_success()
        }
    }
} // Pipeline


/**
 * Gets called when build of the project fails
 */
def slack_error_build() {
    slack_report(false, ':x: Souschef could not be built.', null, 'Build')
}


/**
 * Gets called when tests fail
 */
def slack_error_test() {
    slack_report(false, ':x: Tests failed', null, 'Unit Test')
}

/**
 * Gets called when the long running unit tests fail
 */
def slack_error_long_test() {
    slack_report(false, ':x: Long unit tests failed', null, 'Long Unit Tests')
}

/**
 * Gets called when integration tests fail
 */
def slack_error_integration_test() {
    slack_report(false, ':x: Integration tests failed', null, 'Integration Tests')
}

/**
 * Gets called when sonar fails
 */
def slack_error_sonar() {
    slack_report(false, ':x: Sonar failed', null, 'SonarQube analysis')
}

/**
 * Gets called when build succeeds
 */
def slack_success() {
    slack_report(true, ':heavy_check_mark: Build succeeded', null, '')
}

/**
 * Gets called when all steps up till static analysis succeed
 */
def slack_success_part1() {
    slack_report(true, ':white_check_mark: Part 1 of Build succeeded', null, '')
}

/**
 * Gets called when generating javadoc failed
 */
def slack_error_doc() {
    slack_report(true, ':x: Javadoc generation failed', null, '')
}


/**
 * Find name of author
 * @param  email the commit author's email
 * @return       A string containing the author's name
 */
static String author_name(String email) {
    if (email.startsWith("robbe")) {
        return "Robbe"
    } else if (email.startsWith("jeroen")) {
        return "Jeroen"
    } else if (email.startsWith("jonas.ta")) {
        return "Tack"
    } else if (email.startsWith("jonas.cu")) {
        return "Cuypers"
    } else if (email.startsWith("smidpauw")) {
        return "Stijn"
    } else if (email.startsWith("piet")) {
        return "Piet"
    } else if (email.startsWith("yarne")) {
        return "Yarne"
    } else if (email.startsWith("luca")) {
        return "Luca"
    }

    // Fallback
    return email
}


/**
 * Report results of pipeline to slack
 * @param  successful     was build successful
 * @param  text           Message to display
 * @param  fields         extra fields to include in the message
 * @param  failedStage='' If build failed, at which stage did it fail
 * @return                void
 */
def slack_report(boolean successful, String text, JSONArray fields, failedStage='') {
    JSONArray attachments = new JSONArray()
    JSONObject attachment = new JSONObject()

    // Get email of commit author
    String author_email = sh(script: 'git show -s --pretty=%ae | head -n1', returnStdout: true).trim()

    // Get commit info
    String commit = env.GIT_COMMIT
    String commit_branch = env.BRANCH_NAME
    String commit_hash = "#" + commit.substring(0, 8)
    String commit_message = sh(script: 'git show -s --pretty=%B | head -n1', returnStdout: true).trim()

    // Configure author information
    attachment.put('author_name', author_name(author_email))

    // Configure message details
    attachment.put('color', successful ? 'good' : 'danger')
    attachment.put('title', commit_message + ' @ branch ' + env.BRANCH_NAME)
    attachment.put('title_link', 'https://github.ugent.be/Aurora/aurora/commit/' + commit)
    if (text != '') {
        attachment.put('text', text)
    }

    JSONArray actions = new JSONArray()

    // Add actions to message
    JSONObject actionViewBuild = new JSONObject()

    // Add a button 'build log' to message that links to Jenkins
    actionViewBuild.put('type', 'button')
    actionViewBuild.put('text', 'Build log')
    actionViewBuild.put('url', env.BUILD_URL)

    // Add if build succeeded or failed
    if (successful) {
        attachment.put('fallback', 'Run succeeded @ ' + commit_branch + '.')

        actionViewBuild.put('style', 'primary')
    } else {
        attachment.put('fallback', failedStage + ' failed @ ' + commit_branch + '.')

        actionViewBuild.put('style', 'danger')
    }

    actions.add(actionViewBuild)

    // Add a button 'sonar log' to message that links to sonar (if sonar failed or if build was successful)
    if (successful || failedStage == 'SonarQube analysis') {
        JSONObject actionViewSonar = new JSONObject()
        actionViewSonar.put('type', 'button')
        actionViewSonar.put('text', 'Sonar log')
        actionViewSonar.put('url', 'http://sonarqube.aurora-files.ml/projects')

        if (successful) {
            actionViewSonar.put('style', 'primary')
        } else {
            actionViewSonar.put('style', 'danger')
        }

        actions.add(actionViewSonar)
    }

    attachment.put('actions', actions)

    // Add fields to message
    if (fields != null) {
        attachment.put('fields', fields)
    }

    // Add commit to message
    attachment.put('footer', commit)

    // Add github logo to message
    attachment.put('footer_icon', 'https://github.githubassets.com/images/modules/logos_page/Octocat.png')

    attachments.add(attachment)

    String token = successful ? 'TD60N85K8/BG960T35H/zH59dbicld2uw5Tfdaipg0oL' : 'TD60N85K8/BGABQ0CS3/xS539cEwbxr6cMPvk7LMu7Ve'

    slackSend(channel: '#souschef-builds', attachments: attachments.toString(), teamDomain: 'aurora1819', baseUrl: 'https://hooks.slack.com/services/', token: token)
}
