Gem::Specification.new do |spec|
  spec.name          = "CoderBroker"
  spec.version       = "2.0.8"
  spec.authors       = ["zerosoft"]
  spec.email         = ["zerosoft@vip.qq.com"]

  spec.summary       = "CoderBroker相关使用及二次开发说明"
  spec.license       = "MIT"
  spec.homepage      = "https://github.com/zerosoft/CodeBroker"

  spec.files         = `git ls-files -z`.split("\x0").select { |f| f.match(%r!^(assets|_layouts|_includes|_sass|LICENSE|README)!i) }

  spec.add_runtime_dependency "github-pages", "~> 207"
end
