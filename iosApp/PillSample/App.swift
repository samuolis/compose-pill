import SwiftUI
import PillSample

struct ComposeContent: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController { MainKt.mainViewController() }
    func updateUIViewController(_ controller: UIViewController, context: Context) {}
}

@main
struct PillSampleApp: App {
    var body: some Scene { WindowGroup { ComposeContent().ignoresSafeArea(.keyboard) } }
}
