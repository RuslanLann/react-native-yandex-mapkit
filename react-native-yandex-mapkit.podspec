require 'json'

package = JSON.parse(File.read(File.join(__dir__, 'package.json')))

Pod::Spec.new do |s|
  s.name         = package['name']
  s.version      = package['version']
  s.summary      = package['description']

  s.homepage     = package['homepage']
  s.license      = package['license']
  s.platform     = :ios, "9.0"
  s.authors      = { "svbutko" => "svbutko@hotmail.com" }
  s.source       = { :git => package['repository']['url'] }
  s.source_files  = "ios/**/*.{h,m}"

  s.dependency 'React'
  s.dependency 'YandexMapKit', '3.5'
  s.dependency 'YandexMapKitSearch', '3.5'
  s.dependency 'YandexMapKitDirections', '3.5'
  s.dependency 'YandexRuntime', '3.5'
end

  