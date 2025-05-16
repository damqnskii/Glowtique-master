    document.addEventListener('DOMContentLoaded', () => {
    const voucherForm = document.querySelector('form[action="/cart/voucher-use"]');
    const voucherInput = document.querySelector('#voucher-code');
    const voucherButton = voucherForm.querySelector('.voucher-button');
    const totalPriceElement = document.querySelector('.total-price strong');

    let messageElement = document.getElementById('voucher-message');
    if (!messageElement) {
        messageElement = document.createElement('div');
        messageElement.id = 'voucher-message';
        messageElement.style.marginTop = '0.625rem';
        messageElement.style.fontWeight = 'bold';
        messageElement.style.textAlign = 'center';
        messageElement.style.paddingBottom = '1.25rem';
        voucherForm.appendChild(messageElement);
    }

    voucherForm.addEventListener('submit', async (event) => {
    event.preventDefault();

    const voucherCode = voucherInput.value.trim();
        if (!voucherCode) {
        messageElement.textContent = 'Моля, въведете код за отстъпка.';
        messageElement.style.color = 'red';
        return;
    }

    voucherButton.disabled = true;
    messageElement.textContent = '';

    try {
        const response = await fetch('/cart/voucher-use', {
        method: 'POST',
        headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
    },
        body: new URLSearchParams({
        '_method': 'put',
        'voucher-name': voucherCode
        })
    });

    const result = await response.json();

    if (response.ok && result.success) {

        totalPriceElement.textContent = parseFloat(result.newTotalPrice).toFixed(2) + ' лв.';
        messageElement.style.color = 'green';
        messageElement.textContent = 'Ваучерът беше приложен успешно!';
        } else {
            messageElement.style.color = '#b91919';
            messageElement.textContent = result.error || 'Невалиден или използван код.';
        }
        } catch (err) {
            console.error('Voucher apply error:', err);
            messageElement.style.color = '#b91919';
            messageElement.textContent = 'Грешка при връзката със сървъра.';
        } finally {
            voucherButton.disabled = false;
        }
    });
});
