import Foundation

@objc(AppUpdate)
class AppUpdate: NSObject {

  @objc(multiply:withB:withResolver:withRejecter:)
  func multiply(a: Float, b: Float, resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
    resolve(a*b)
  }

    private static let updateAvailabilityNotAvailable = 1
    private static let updateAvailabilityAvailable = 2
   
    @objc
    func getAppUpdateInfo(_ name: String, resolve:@escaping RCTPromiseResolveBlock,reject:@escaping RCTPromiseRejectBlock)  -> Void {
        DispatchQueue.global().async {
            do {
                guard
                    let info = Bundle.main.infoDictionary,
                    let bundleId = info["CFBundleIdentifier"] as? String,
                    let currentVersion = info["CFBundleShortVersionString"] as? String,
                    let lookupUrl = URL(string: "https://itunes.apple.com/lookup?bundleId=\(bundleId)")
                else {
                    reject("Invalid bundle info provided","Invalid bundle info provided", nil)
                    return
                }
              
                let data = try Data(contentsOf: lookupUrl)
                guard
                    let json = try JSONSerialization.jsonObject(with: data, options: [.allowFragments]) as? [String: Any],
                    let result = (json["results"] as? [Any])?.first as? [String: Any],
                    let availableVersion = result["version"] as? String,
                    let availableVersionReleaseDate = result["currentVersionReleaseDate"] as? String,
                    let minimumOsVersion = result["minimumOsVersion"] as? String
                else {
                    reject("Required app information could not be fetched","Required app information could not be fetched",nil)
                    return
                }
                var updateAvailability = AppUpdate.updateAvailabilityNotAvailable
                let updateAvailable = self.compareVersions(currentVersion, availableVersion) == .orderedDescending
                if updateAvailable {
                    updateAvailability = AppUpdate.updateAvailabilityAvailable
                }
                resolve([
                    "currentVersion": currentVersion,
                    "availableVersion": availableVersion,
                    "availableVersionReleaseDate": availableVersionReleaseDate,
                    "updateAvailability": updateAvailability,
                    "minimumOsVersion": minimumOsVersion
                ])
            } catch let error {
                reject(error.localizedDescription,error.localizedDescription, nil)
            }
        }
    }

    @objc func openAppStore(_ name: String,resolve:@escaping RCTPromiseResolveBlock,reject:@escaping RCTPromiseRejectBlock)  -> Void {
        DispatchQueue.global().async {
            do {
                guard
                    let info = Bundle.main.infoDictionary,
                    let bundleId = info["CFBundleIdentifier"] as? String,
                    var lookupUrl = URL(string: "https://itunes.apple.com/lookup?bundleId=\(bundleId)")
                else {
                    reject("Invalid bundle info provided","Invalid bundle info provided",nil)
                    return
                }
             
                let data = try Data(contentsOf: lookupUrl)
                guard
                    let json = try JSONSerialization.jsonObject(with: data, options: [.allowFragments]) as? [String: Any],
                    let result = (json["results"] as? [Any])?.first as? [String: Any],
                    let trackId = result["trackId"] as? Int,
                    let storeUrl = URL(string: "itms-apps://itunes.apple.com/app/id\(trackId)")
                else {
                    reject("Required app information could not be fetched","Required app information could not be fetched", nil)
                    return
                }
                DispatchQueue.main.async {
                    UIApplication.shared.open(storeUrl) { (_) in
                        resolve(true)
                    }
                }
            } catch let error {
                reject(error.localizedDescription,error.localizedDescription,nil)
            }
        }
    }
    
    @objc func compareVersions(_ currentVersion: String, _ availableVersion: String) -> ComparisonResult {
           return availableVersion.compare(currentVersion, options: .numeric)
       }
    
}
