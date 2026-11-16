import XCTest

final class SmokeTests: XCTestCase {
    func testNavigationAndAccessibleLabels() {
        let app = XCUIApplication()
        app.launch()
        let discover = app.buttons["Discover"]
        let saved = app.buttons["Saved"]
        XCTAssertTrue(discover.waitForExistence(timeout: 30))
        XCTAssertTrue(discover.isSelected)
        XCTAssertTrue(saved.exists)
        saved.tap()
        XCTAssertTrue(app.staticTexts["Your saved collection, at a glance."].waitForExistence(timeout: 10))
        XCTAssertTrue(saved.isSelected)
    }
}
