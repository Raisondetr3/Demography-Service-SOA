import requests
from requests.auth import HTTPDigestAuth
import json

url = "http://localhost:9990/management"
username = "user222"
password = "user222"
pool_name = "slsb-strict-max-pool"

payload = {
    "operation": "read-resource",
    "address": ["subsystem", "ejb3", "strict-max-bean-instance-pool", "slsb-strict-max-pool"],
    "include-runtime": True
}

# Используем Digest Auth (обязательно!)
resp = requests.post(
    url,
    json=payload,
    auth=HTTPDigestAuth(username, password),
    headers={"Content-Type": "application/json"}
)
print(resp)
try:
    data = resp.json()
except json.JSONDecodeError:
    print("Сервер вернул не-JSON:")
    print("Status:", resp.status_code)
    print("Headers:", resp.headers)
    print("Тело ответа:", resp.text[:500])  # первые 500 символов
    exit(0)

if data.get("outcome") == "success":
    attrs = data["result"]
    print(f"Пул: {pool_name}")
    print(attrs)
#     print(f"  Текущий размер: {attrs['CurrentSize']}")
#     print(f"  Активных:       {attrs['ActiveCount']}")
#     print(f"  Свободных:      {attrs['AvailableCount']}")
#     print(f"  Максимум:       {attrs['MaxPoolSize']}")
#     utilization = attrs['ActiveCount'] / attrs['MaxPoolSize'] if attrs['MaxPoolSize'] > 0 else 0
#     print(f"  Использование:  {utilization:.1%}")
else:
    print("Ошибка:", data)