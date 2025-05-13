// RSA加密工具类
class RSAUtils {
    constructor() {
        this.encrypt = new JSEncrypt();
    }

    // 设置公钥
    setPublicKey(publicKey) {
        this.encrypt.setPublicKey(publicKey);
    }

    // 加密数据
    encrypt(data) {
        return this.encrypt.encrypt(data);
    }
}

// 创建全局实例
window.RSAUtils = new RSAUtils(); 