## v.0.7.0
May 20, 2022

- Implemented new localization API
- Optimized a image preparation for the neural network 
- Minor fixes

## v.0.6.1
February 9, 2022

- VPS service keeps running even when getting HttpException
- Intrinsics matrix and camera buffer now scales correctly
- Minor fixes

## v.0.6.0
January 18, 2022

- Added georeferencing
- Added Open Street Map support in an example app
- Read input parameters from tf-model
- Replaced forked SceneForm library Google by [Sceneform Maintained](https://github.com/SceneView/sceneform-android)

## v.0.5.0
December 9, 2021

- Fixed getting euler angles from Quaternion(YXZ-order; intrinsic rotations)
- Sceneform source moved into `vps-sdk` module
- Added default configs for VPS config - `indoor` and `outdoor`
