import XCTest

final class SmokeTests: XCTestCase {
    func testNavigationAndAccessibleLabels() {
        let app = XCUIApplication()
        app.launch()
        let home = app.buttons["Home"]
        let messages = app.buttons["Messages"]
        XCTAssertTrue(home.waitForExistence(timeout: 30))
        XCTAssertTrue(home.isSelected)
        XCTAssertTrue(messages.exists)
        messages.tap()
        XCTAssertTrue(app.staticTexts["messages · Item 1"].waitForExistence(timeout: 10))
        XCTAssertTrue(messages.isSelected)
    }
}
