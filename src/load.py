import requests
import time
import logging

logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger()

URL = "http://localhost:8080/api/demography/hair-color/BLUE/percentage"

def send_request():
    try:
        response = requests.get(URL, timeout=5)
        logger.info(f"Status: {response.status_code}, Response: {response.text[:100]}")
    except requests.exceptions.Timeout:
        logger.error("Request timed out")
    except requests.exceptions.ConnectionError:
        logger.error("Connection error — is the server running?")
    except Exception as e:
        logger.error(f"Unexpected error: {e}")

if __name__ == "__main__":
    logger.info("Starting request loop (Ctrl+C to stop)...")
    try:
        while True:
            send_request()
#             time.sleep(0.05)
    except KeyboardInterrupt:
        logger.info("Stopped by user.")
