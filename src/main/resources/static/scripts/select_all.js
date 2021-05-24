
select_all(this)

function select_all(source)
{
    let checkboxes = document.getElementsByName('idChecked');
    for (let i = 0; i < checkboxes.length; i++)
    {
        checkboxes[i].checked = source.checked;
    }
}