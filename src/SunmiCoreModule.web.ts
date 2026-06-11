import { registerWebModule, NativeModule } from 'expo';

class SunmiCoreModule extends NativeModule<{}> {}

export default registerWebModule(SunmiCoreModule, 'SunmiCoreModule');
