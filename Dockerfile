FROM alpine:latest

RUN apk update && \
    apk add --no-cache python3 py3-pip jq curl unzip git openssh ca-certificates && \
    update-ca-certificates

RUN pip install --upgrade pip

RUN pip install boto3 botocore awscli

RUN latest_version=$(curl -s https://api.github.com/repos/hashicorp/terraform/releases/latest | jq -r '.tag_name') && \
    curl -fsSL -o /tmp/terraform.zip "https://releases.hashicorp.com/terraform/${latest_version#v}/terraform_${latest_version#v}_linux_amd64.zip" && \
    unzip /tmp/terraform.zip -d /usr/local/bin && \
    rm /tmp/terraform.zip

CMD ["sh", "-c", "terraform version && python3 --version && pip --version && aws --version"]