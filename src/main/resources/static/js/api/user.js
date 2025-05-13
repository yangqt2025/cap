// 用户相关API
const userApi = {
    // 获取公钥
    async getPublicKey() {
        const response = await axios.get('/api/user/public-key');
        window.RSAUtils.setPublicKey(response.data);
        return response.data;
    },

    // 用户登录
    async login(userName, password) {
        // 确保有公钥
        if (!window.RSAUtils.encrypt.getPublicKey()) {
            await this.getPublicKey();
        }

        // 加密密码
        const encryptedPassword = window.RSAUtils.encrypt(password);
        if (!encryptedPassword) {
            throw new Error('密码加密失败');
        }

        // 发送登录请求
        const response = await axios.post('/api/user/login', {
            userName,
            userPassword: encryptedPassword
        });

        return response.data;
    },

    // 用户注册
    async register(userName, password, phoneNumber) {
        // 确保有公钥
        if (!window.RSAUtils.encrypt.getPublicKey()) {
            await this.getPublicKey();
        }

        // 加密密码
        const encryptedPassword = window.RSAUtils.encrypt(password);
        if (!encryptedPassword) {
            throw new Error('密码加密失败');
        }

        // 发送注册请求
        const response = await axios.post('/api/user/register', {
            userName,
            userPassword: encryptedPassword,
            phoneNumber
        });

        return response.data;
    }
};

// 创建全局实例
window.userApi = userApi; 