from flask import Flask, request, jsonify
import random

app = Flask(__name__)

@app.route('/ai/process', methods=['POST'])
def process_answer():
    # 获取请求数据
    data = request.json
    
    # 生成随机分数（0-10分）
    content_score = random.randint(0, 10)
    format_score = random.randint(0, 10)
    logic_score = random.randint(0, 10)
    grammar_score = random.randint(0, 10)
    
    # 生成建议
    suggestions = [
        "答案结构清晰，但可以增加更多细节。",
        "语言表达流畅，但逻辑性有待加强。",
        "内容完整，但格式需要调整。",
        "分析深入，但建议增加实例支持。",
        "整体表现不错，继续保持。"
    ]
    suggestion = random.choice(suggestions)
    
    # 构建响应
    response = {
        "id": data.get("id"),
        "userId": data.get("userId"),
        "questionId": data.get("questionId"),
        "userAnswer": data.get("userAnswer"),
        "contentScore": content_score,
        "formatScore": format_score,
        "logicScore": logic_score,
        "grammarScore": grammar_score,
        "suggestion": suggestion,
        "createTime": data.get("createTime"),
        "updateTime": data.get("updateTime"),
        "isDelete": data.get("isDelete", 0)
    }
    
    return jsonify(response)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8101) 