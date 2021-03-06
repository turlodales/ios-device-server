package com.badoo.automation.deviceserver.ios.fbsimctl

import org.hamcrest.Matchers.*
import org.junit.Assert.*
import org.junit.Test

class FbSimctlResponseParserTest {
    private val simulatorsListString = """
        {"event_name":"list","timestamp":1516204149,"subject":{"arch":"x86_64","state":"Shutdown","model":"iPhone X","name":"iPhone X","udid":"54BC5B1F-7144-450C-8459-D61C2206D1F4","os":"iOS 11.2"},"event_type":"discrete"}
        {"event_name":"list","timestamp":1516204149,"subject":{"arch":"x86_64","state":"Booted","model":"iPhone 8","name":"iPhone 8","udid":"4B740F75-D83E-4DBA-8BA1-1A82A68FA27E","os":"iOS 11.2"},"event_type":"discrete"}
        {"event_name":"list","timestamp":1516204149,"subject":{"arch":"x86_64","state":"Shutdown","model":"iPad Pro (12.9-inch) (2nd generation)","name":"iPad Pro (12.9-inch) (2nd generation)","udid":"82D60C16-CBF7-4AB0-85EA-4FB778C0D7CD","os":"iOS 11.2"},"event_type":"discrete"}
    """.trimIndent()

    private val listAppsString = """
        {"event_name":"list_apps","timestamp":1516247902,"subject":[{"data_container":null,"bundle":{"path":"\/Applications\/Xcode.app\/Contents\/Developer\/Platforms\/iPhoneOS.platform\/Developer\/Library\/CoreSimulator\/Profiles\/Runtimes\/iOS.simruntime\/Contents\/Resources\/RuntimeRoot\/Applications\/MobileSlideShow.app","bundle_id":"com.apple.mobileslideshow","binary":{"name":"MobileSlideShow","path":"\/Applications\/Xcode.app\/Contents\/Developer\/Platforms\/iPhoneOS.platform\/Developer\/Library\/CoreSimulator\/Profiles\/Runtimes\/iOS.simruntime\/Contents\/Resources\/RuntimeRoot\/Applications\/MobileSlideShow.app\/MobileSlideShow","architectures":["x86_64"]},"name":"MobileSlideShow"},"install_type":"system"},{"data_container":"\/Users\/user\/Library\/Developer\/CoreSimulator\/Devices\/4B740F75-D83E-4DBA-8BA1-1A82A68FA27E\/data\/Containers\/Data\/Application\/B6B0251A-282C-422B-B379-3D6A1052B620","bundle":{"path":"\/Applications\/Xcode.app\/Contents\/Developer\/Platforms\/iPhoneOS.platform\/Developer\/Library\/CoreSimulator\/Profiles\/Runtimes\/iOS.simruntime\/Contents\/Resources\/RuntimeRoot\/Applications\/Maps.app","bundle_id":"com.apple.Maps","binary":{"name":"Maps","path":"\/Applications\/Xcode.app\/Contents\/Developer\/Platforms\/iPhoneOS.platform\/Developer\/Library\/CoreSimulator\/Profiles\/Runtimes\/iOS.simruntime\/Contents\/Resources\/RuntimeRoot\/Applications\/Maps.app\/Maps","architectures":["x86_64"]},"name":"Maps"},"install_type":"system"},{"data_container":null,"bundle":{"path":"\/Applications\/Xcode.app\/Contents\/Developer\/Platforms\/iPhoneOS.platform\/Developer\/Library\/CoreSimulator\/Profiles\/Runtimes\/iOS.simruntime\/Contents\/Resources\/RuntimeRoot\/Applications\/MobileSMS.app","bundle_id":"com.apple.MobileSMS","binary":{"name":"MobileSMS","path":"\/Applications\/Xcode.app\/Contents\/Developer\/Platforms\/iPhoneOS.platform\/Developer\/Library\/CoreSimulator\/Profiles\/Runtimes\/iOS.simruntime\/Contents\/Resources\/RuntimeRoot\/Applications\/MobileSMS.app\/MobileSMS","architectures":["x86_64"]},"name":"MobileSMS"},"install_type":"system"},{"data_container":"\/Users\/user\/Library\/Developer\/CoreSimulator\/Devices\/4B740F75-D83E-4DBA-8BA1-1A82A68FA27E\/data\/Containers\/Data\/Application\/1F486790-36FA-4DA1-8BC4-EC347466C06A","bundle":{"path":"\/Users\/user\/Library\/Developer\/CoreSimulator\/Devices\/4B740F75-D83E-4DBA-8BA1-1A82A68FA27E\/data\/Containers\/Bundle\/Application\/DE199219-B5B3-4DAA-92E4-37A9476B4885\/Bumble.app","bundle_id":"com.moxco.bumble","binary":{"name":"Bumble","path":"\/Users\/user\/Library\/Developer\/CoreSimulator\/Devices\/4B740F75-D83E-4DBA-8BA1-1A82A68FA27E\/data\/Containers\/Bundle\/Application\/DE199219-B5B3-4DAA-92E4-37A9476B4885\/Bumble.app\/Bumble","architectures":["x86_64"]},"name":"Bumble"},"install_type":"user"},{"data_container":"\/Users\/user\/Library\/Developer\/CoreSimulator\/Devices\/4B740F75-D83E-4DBA-8BA1-1A82A68FA27E\/data\/Containers\/Data\/Application\/48CF1917-F972-464E-9EA1-EF9C4140FEBB","bundle":{"path":"\/Applications\/Xcode.app\/Contents\/Developer\/Platforms\/iPhoneOS.platform\/Developer\/Library\/CoreSimulator\/Profiles\/Runtimes\/iOS.simruntime\/Contents\/Resources\/RuntimeRoot\/Applications\/MobileSafari.app","bundle_id":"com.apple.mobilesafari","binary":{"name":"MobileSafari","path":"\/Applications\/Xcode.app\/Contents\/Developer\/Platforms\/iPhoneOS.platform\/Developer\/Library\/CoreSimulator\/Profiles\/Runtimes\/iOS.simruntime\/Contents\/Resources\/RuntimeRoot\/Applications\/MobileSafari.app\/MobileSafari","architectures":["x86_64"]},"name":"MobileSafari"},"install_type":"system"}],"event_type":"discrete"}
        {"event_type":"started","subject":{},"timestamp":1516247902,"target":{"arch":"x86_64","state":"Booted","model":"iPhone 8","name":"iPhone 8","udid":"4B740F75-D83E-4DBA-8BA1-1A82A68FA27E","os":"iOS 11.2"},"event_name":"list_apps"}
        {"event_type":"ended","subject":{},"timestamp":1516247902,"target":{"arch":"x86_64","state":"Booted","model":"iPhone 8","name":"iPhone 8","udid":"4B740F75-D83E-4DBA-8BA1-1A82A68FA27E","os":"iOS 11.2"},"event_name":"list_apps"}
    """.trimIndent()

    // fbsimctl --json FA2EC53F-E71A-4BAF-8686-840813C5348F diagnose
    private val diagnoseRunningStrings = """
        {"event_type":"started","subject":{"type":"all"},"timestamp":1516634884,"target":{"arch":"x86_64","state":"Booted","model":"iPhone 6","name":"iPhone 6","udid":"FA2EC53F-E71A-4BAF-8686-840813C5348F","os":"iOS 11.0"},"event_name":"diagnose"}
        {"event_type":"ended","subject":{"type":"all"},"timestamp":1516634884,"target":{"arch":"x86_64","state":"Booted","model":"iPhone 6","name":"iPhone 6","udid":"FA2EC53F-E71A-4BAF-8686-840813C5348F","os":"iOS 11.0"},"event_name":"diagnose"}
        {"event_name":"diagnostic","timestamp":1516634884,"subject":{"short_name":"system_log","human_name":"System Log","file_type":"log","location":"\/Users\/user\/Library\/Logs\/CoreSimulator\/FA2EC53F-E71A-4BAF-8686-840813C5348F\/system.log"},"event_type":"discrete"}
        {"event_name":"diagnostic","timestamp":1516634884,"subject":{"short_name":"coresimulator","human_name":"Core Simulator Log","file_type":"log","location":"\/Users\/user\/Library\/Logs\/CoreSimulator\/CoreSimulator.log"},"event_type":"discrete"}
        {"event_name":"diagnostic","timestamp":1516634884,"subject":{"short_name":"launchd_bootstrap","human_name":"Launchd Bootstrap","file_type":"plist","location":"\/Users\/user\/Library\/Developer\/CoreSimulator\/Devices\/FA2EC53F-E71A-4BAF-8686-840813C5348F\/data\/var\/run\/launchd_bootstrap.plist"},"event_type":"discrete"}
    """.trimIndent()

    private val diagnoseShutdownStrings = """
        {"event_type":"started","subject":{"type":"all"},"timestamp":1516635037,"target":{"arch":"x86_64","state":"Shutdown","model":"iPhone X","name":"iPhone X","udid":"54BC5B1F-7144-450C-8459-D61C2206D1F4","os":"iOS 11.2"},"event_name":"diagnose"}
        {"event_type":"ended","subject":{"type":"all"},"timestamp":1516635037,"target":{"arch":"x86_64","state":"Shutdown","model":"iPhone X","name":"iPhone X","udid":"54BC5B1F-7144-450C-8459-D61C2206D1F4","os":"iOS 11.2"},"event_name":"diagnose"}
        {"event_name":"diagnostic","timestamp":1516635037,"subject":{"short_name":"coresimulator","human_name":"Core Simulator Log","file_type":"log","location":"\/Users\/user\/Library\/Logs\/CoreSimulator\/CoreSimulator.log"},"event_type":"discrete"}
    """.trimIndent()

    private val simulatorCreateStrings = """
        {"event_name":"create","timestamp":1521028581,"subject":{"device":"iPhone 6","os":"iOS 11.2","aux_directory":null,"architecture":"x86_64"},"event_type":"started"}
        {"event_name":"log","timestamp":1521028581,"level":"info","subject":"Did Change State => Booting","event_type":"discrete"}
        {"event_name":"log","timestamp":1521028581,"level":"info","subject":"Did Change State => Booted","event_type":"discrete"}
        {"event_name":"log","timestamp":1521028581,"level":"info","subject":"Simulator Did launch => Process launchd_sim | PID 88778","event_type":"discrete"}
        {"event_name":"create","timestamp":1521028581,"subject":{"pid":0,"arch":"x86_64","os":"iOS 11.2","container-pid":0,"model":"iPhone 6","udid":"7CA9DCE7-22A2-434B-A9EE-3E2A497E3881","name":"iPhone 6","state":"Shutdown"},"event_type":"ended"}
    """.trimIndent()

    private val appInstallFailure = """
        { "event_name": "log", "timestamp": 1537374100, "level": "info", "subject": "Running \/usr\/bin\/xcode-select --print-path with environment {\n HOME = \"\/Users\/vfrolov\";\n}", "event_type": "discrete" }
        { "event_type": "started", "subject": { "application_path": "\/Users\/vfrolov\/GitHub\/ios-device-server\/ios\/facebook\/simulators-Facebook-c233bf07-x10\/WebDriverAgentRunner-Runner.app\/aasdfasdfasdf", "codesign": false }, "timestamp": 1537374100, "target": { "arch": "x86_64", "state": "Booted", "model": "iPhone 6", "name": "iPhone 6", "udid": "AB3C83FF-C05F-4CAA-9199-18E6C87FA9FB", "os": "iOS 11.4" }, "event_name": "install" }
        { "event_name": "failure", "timestamp": 1537374100, "subject": "Error Domain=com.facebook.FBControlCore Code=0 \"File at path \/Users\/vfrolov\/GitHub\/ios-device-server\/ios\/facebook\/simulators-Facebook-c233bf07-x10\/WebDriverAgentRunner-Runner.app\/aasdfasdfasdf is neither an IPA not a .app\" UserInfo={NSLocalizedDescription=File at path \/Users\/vfrolov\/GitHub\/ios-device-server\/ios\/facebook\/simulators-Facebook-c233bf07-x10\/WebDriverAgentRunner-Runner.app\/aasdfasdfasdf is neither an IPA not a .app, NSUnderlyingError=0x7fb40a114ba0 {Error Domain=com.facebook.FBControlCore Code=0 \"Failed to open \/Users\/vfrolov\/GitHub\/ios-device-server\/ios\/facebook\/simulators-Facebook-c233bf07-x10\/WebDriverAgentRunner-Runner.app\/aasdfasdfasdf for reading\" UserInfo={NSLocalizedDescription=Failed to open \/Users\/vfrolov\/GitHub\/ios-device-server\/ios\/facebook\/simulators-Facebook-c233bf07-x10\/WebDriverAgentRunner-Runner.app\/aasdfasdfasdf for reading}}}", "event_type": "discrete" }]
    """.trimIndent()

    private val appInstallSuccess = """
        { "event_name": "log", "timestamp": 1537373327, "level": "info", "subject": "Running \/usr\/bin\/xcode-select --print-path with environment {\n HOME = \"\/Users\/vfrolov\";\n}", "event_type": "discrete" }
        { "event_type": "started", "subject": { "application_path": "\/Users\/vfrolov\/GitHub\/ios-device-server\/ios\/facebook\/simulators-Facebook-c233bf07-x10\/WebDriverAgentRunner-Runner.app", "codesign": false }, "timestamp": 1537373327, "target": { "arch": "x86_64", "state": "Booted", "model": "iPhone 6", "name": "iPhone 6", "udid": "AB3C83FF-C05F-4CAA-9199-18E6C87FA9FB", "os": "iOS 11.4" }, "event_name": "install" }
        { "event_type": "ended", "subject": { "application_path": "\/Users\/vfrolov\/GitHub\/ios-device-server\/ios\/facebook\/simulators-Facebook-c233bf07-x10\/WebDriverAgentRunner-Runner.app", "codesign": false }, "timestamp": 1537373330, "target": { "arch": "x86_64", "state": "Booted", "model": "iPhone 6", "name": "iPhone 6", "udid": "AB3C83FF-C05F-4CAA-9199-18E6C87FA9FB", "os": "iOS 11.4" }, "event_name": "install" }
     """.trimIndent()

    @Test fun parseList() {
        val parsedValue = FBSimctlResponseParser().parse(simulatorsListString)
        assertEquals("Wrong element count", 3, parsedValue.size)
        val deviceAsMap = parsedValue[0]["subject"] as Map<*, *>
        assertEquals("54BC5B1F-7144-450C-8459-D61C2206D1F4", deviceAsMap["udid"])
    }

    @Test fun parseCreateDevice() {
        val parsedValue = FBSimctlResponseParser().parseDeviceCreation(simulatorCreateStrings, false)
        assertEquals("7CA9DCE7-22A2-434B-A9EE-3E2A497E3881", parsedValue.udid)
    }

    @Test fun installAppSuccess() {
        val result = FBSimctlResponseParser().parseInstallApp(appInstallSuccess)
        assertTrue("Wrong install result", result.isSuccess)
        assertTrue("Wrong install error message", result.errorMessage.isBlank())
    }

    @Test fun installAppFail() {
        val result = FBSimctlResponseParser().parseInstallApp(appInstallFailure)
        assertFalse("Wrong install result", result.isSuccess)
        assertThat(result.errorMessage, containsString("is neither an IPA not a .app"))
    }

    @Test fun parseDeviceList() {
        val parsedValue = FBSimctlResponseParser().parseDeviceList(simulatorsListString)
        val fbSimctlDevice = parsedValue[0]

        assertEquals("Wrong element count", 3, parsedValue.size)
        assertThat(fbSimctlDevice, instanceOf(FBSimctlDevice::class.java))
        assertThat(fbSimctlDevice.udid, equalTo("54BC5B1F-7144-450C-8459-D61C2206D1F4"))
    }

    @Test fun parseDeviceListWithUntrustedDevice() {
        val list = """{"event_name":"list","timestamp":1527060706,"subject":{"state":"Booted","udid":"1aa0a00a0a00aaa000a000a00a0a000a0000a000"},"event_type":"discrete"}"""

        val parsedValue = FBSimctlResponseParser().parseDeviceList(list)
        val fbSimctlDevice = parsedValue[0]

        assertEquals("Wrong element count", 1, parsedValue.size)
        assertThat(fbSimctlDevice, instanceOf(FBSimctlDevice::class.java))
        assertThat(fbSimctlDevice.udid, equalTo("1aa0a00a0a00aaa000a000a00a0a000a0000a000"))
    }

    @Test fun parseAppsList() {
        val bumbleAppIndex = 3
        val parsedValue = FBSimctlResponseParser().parseApplicationsList(listAppsString)
        val fbSimctlAppInfo = parsedValue[bumbleAppIndex]

        assertEquals("Wrong element count", 5, parsedValue.size)
        assertThat(fbSimctlAppInfo, instanceOf(FBSimctlAppInfo::class.java))
        assertThat(fbSimctlAppInfo.bundle.bundle_id, equalTo("com.moxco.bumble"))
    }

    @Test fun diagnoseSyslogLocationRunning() {
        val expectedSyslogLocation = "/Users/user/Library/Logs/CoreSimulator/FA2EC53F-E71A-4BAF-8686-840813C5348F/system.log"
        val parsedValue = FBSimctlResponseParser().parseDiagnosticInfo(diagnoseRunningStrings)
        assertEquals("Wrong syslog location", expectedSyslogLocation, parsedValue.sysLogLocation)
    }

    @Test fun diagnoseSyslogLocationShutdown() {
        val expectedCoreSimulatorLogLocation = "/Users/user/Library/Logs/CoreSimulator/CoreSimulator.log"
        val parsedValue = FBSimctlResponseParser().parseDiagnosticInfo(diagnoseShutdownStrings)
        assertNull("Syslog should be null", parsedValue.sysLogLocation)
        assertEquals("Wrong syslog location", expectedCoreSimulatorLogLocation, parsedValue.coreSimulatorLogLocation)
    }
}
